/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.cgtpropertydisposalsstubs.controllers

import java.io.FileWriter
import java.time.Instant
import java.util
import java.util.UUID

import cats.data.EitherT
import cats.effect.{IO, Resource}
import com.google.inject.Inject
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.{AhcWSResponse, StandaloneAhcWSResponse}
import play.api.mvc.{Action, ControllerComponents, Request}
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{Error, UploadRequest, UpscanInitiateRequest, UpscanUploadMeta}
import uk.gov.hmrc.cgtpropertydisposalsstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

class UpscanController @Inject() (cc: ControllerComponents, wsClient: WSClient)
    extends BackendController(cc)
    with Logging {

  // mimics the initiate service
  def initiate(): Action[JsValue] =
    Action(parse.json) { implicit request =>
      request.body.validateOpt[UpscanInitiateRequest] match {
        case JsSuccess(maybeUpscanMeta, _) =>
          maybeUpscanMeta match {
            case Some(upscanMeta) =>
              val upscanUploadMeta = UpscanUploadMeta(
                UUID.randomUUID().toString,
                UploadRequest(
                  href = "http://localhost:7022/upscan/upload",
                  fields = Map(
                    "success_action_redirect" -> upscanMeta.successRedirect,
                    "error_action_redirect"   -> upscanMeta.errorRedirect,
                    "x-amz-meta-callback-url" -> upscanMeta.callbackUrl
                  )
                )
              )
              logger.info(s"generated upscan meta : $upscanUploadMeta")
              Ok(Json.toJson(upscanUploadMeta))

            case None =>
              logger.error("no upscan meta received from initiate service")
              InternalServerError
          }
        case JsError(errors) =>
          logger.error(s"could not parse upscan initiate request: ${errors.toString}")
          InternalServerError
      }
    }

  // mimics the proxy and notify service
  def upload() =
    Action(parse.multipartFormData).async { implicit request =>
      (
        request.body.dataParts.get("x-amz-meta-callback-url"),
        request.body.dataParts.get("success_action_redirect"),
        request.body.dataParts.get("error_action_redirect")
      ) match {
        case (Some(callBackUrl), Some(successRedirectUrl), Some(_)) =>
          makeCallBackRequest(callBackUrl.head).value map {
            case Left(e) =>
              logger.warn(s"could not make upscan call back: $e")
              InternalServerError
            case Right(_) =>
              logger.info("successfully made upscan call back")
              Redirect(successRedirectUrl.head, 303)
          }
        case _ =>
          logger.warn(s"could not find upscan meta data in request: ${request.body.toString}")
          Future.successful(InternalServerError)
      }
    }

  // mimics the s3 cloud service
  def download() =
    Action {
      val resource = Resource.make {
        IO(new FileWriter("/tmp/uploadedFile.txt"))
      } { out =>
        IO(out.close())
      }
      resource
        .use(out =>
          IO {
            val chars = new Array[Char](3145728)
            util.Arrays.fill(chars, '.')
            chars(3145727) = '\n'
            out.write(chars)
          }
        )
        .attempt
        .map {
          case Left(e) =>
            logger.warn(s"failed to serve file for download:$e")
            InternalServerError
          case Right(_) =>
            Ok.sendFile(new java.io.File("/tmp/uploadedFile.txt"))
        }
        .unsafeRunSync
    }

  // mimic notify service
  def makeCallBackRequest(callbackUrl: String)(implicit request: Request[_]): EitherT[Future, Error, Unit] = {
    val upscanResponse =
      s"""
        |{
        |    "reference" : "${UUID.randomUUID().toString}",
        |    "fileStatus" : "READY",
        |    "downloadUrl" : "${routes.UpscanController.download().absoluteURL()}",
        |    "uploadDetails": {
        |        "uploadTimestamp": "${Instant.now()}",
        |        "checksum": "${UUID.randomUUID().toString}",
        |        "fileName": "${UUID.randomUUID().toString}.pdf",
        |        "fileMimeType": "application/pdf"
        |    }
        |}
        |
        |""".stripMargin

    EitherT(
      wsClient
        .url(callbackUrl)
        .post(Json.parse(upscanResponse))
        .map {
          case AhcWSResponse(underlying) =>
            underlying match {
              case response: StandaloneAhcWSResponse =>
                if (response.status == NO_CONTENT)
                  Right(())
                else
                  Left(Error("upscan call back failure"))
              case _ =>
                Left(Error("unknown http client response"))
            }
          case _ =>
            Left(Error("unknown http client error"))
        }
        .recover {
          case NonFatal(e) => Left(Error(s"http call to make upscan call back failed: $e"))
        }
    )
  }

}
