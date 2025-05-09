/*
 * Copyright 2024 HM Revenue & Customs
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

import com.google.inject.Inject
import org.apache.pekko.actor.{Actor, ActorRef, ActorSystem, Cancellable, Props}
import org.apache.pekko.pattern.ask
import org.apache.pekko.util.Timeout
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.EmailVerificationController.VerificationManager
import uk.gov.hmrc.cgtpropertydisposalsstubs.controllers.EmailVerificationController.VerificationManager.CleanData
import uk.gov.hmrc.cgtpropertydisposalsstubs.models.{EmailVerificationRequest, EmailVerificationRequestWithTimestamp, EmailVerificationRequested, EmailVerificationRequestedAck, GetEmailVerificationRequest, GetEmailVerificationRequestResponse}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.Instant
import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.matching.Regex

class EmailVerificationController @Inject() (
  cc: ControllerComponents,
  system: ActorSystem
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {
  val statusRegex: Regex = "status(\\d{3})@email\\.com".r

  private val verificationManager: ActorRef = system.actorOf(VerificationManager.props())

  implicit val askTimeout: Timeout = Timeout(5.seconds)

  implicit def toFuture[A](a: A): Future[A] = Future.successful(a)

  def verifyEmail(): Action[AnyContent] =
    Action.async { implicit request =>
      request.body.asJson.fold[Future[Result]] {
        logger.warn("No JSON found in body")
        BadRequest
      } { json =>
        json
          .validate[EmailVerificationRequest]
          .fold(
            { errors =>
              logger.warn(s"Could not read body of email verification request: $errors")
              BadRequest
            },
            request =>
              (verificationManager ? EmailVerificationRequested(request)).mapTo[EmailVerificationRequestedAck].map {
                _ =>
                  request.email match {
                    case statusRegex(status) =>
                      logger.info(s"Returning status $status to email verification request: $request")
                      Status(status.toInt)

                    case _ =>
                      logger.info(s"Returning status 201 to email verification request: $request")
                      Created
                  }
              }
          )
      }
    }

  def getEmailVerificationRequest(email: String): Action[AnyContent] =
    Action.async { _ =>
      (verificationManager ? GetEmailVerificationRequest(email))
        .mapTo[GetEmailVerificationRequestResponse]
        .map { response =>
          Ok(Json.toJson(response.request))
        }
    }
}

object EmailVerificationController {
  // Actor which stores verification requests so that the verification requests details can be
  // retrieved back. Verification request are only stored for a finite amount of time and are
  // cleared out periodically
  class VerificationManager extends Actor {
    import context.dispatcher

    override def preStart(): Unit = {
      super.preStart()
      cleanJob = Some(context.system.scheduler.scheduleWithFixedDelay(0.seconds, cleanFrequency, self, CleanData))
    }

    override def postStop(): Unit = {
      super.postStop()
      cleanJob.foreach(_.cancel())
      cleanJob = None
    }

    private val (cleanFrequency, ttlMillis) = 5.minutes -> 30.minutes.toMillis

    private var cleanJob: Option[Cancellable] = None

    def now(): Long = Instant.now().toEpochMilli

    def receive: Receive = active(Map.empty)

    private def active(requests: Map[String, EmailVerificationRequestWithTimestamp]): Receive = {
      case EmailVerificationRequested(r) =>
        context become active(requests.updated(r.email, EmailVerificationRequestWithTimestamp(r, now())))
        sender() ! EmailVerificationRequestedAck()

      case GetEmailVerificationRequest(e) =>
        sender() ! GetEmailVerificationRequestResponse(requests.get(e).map(_.request))

      case CleanData =>
        context become active(requests.filter(_._2.timestamp > (now() - ttlMillis)))
    }
  }

  object VerificationManager {
    def props(): Props = Props(new VerificationManager)

    private case object CleanData
  }
}
