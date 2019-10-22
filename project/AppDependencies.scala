import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"   %% "bootstrap-play-26"   % "0.42.0",
    "uk.gov.hmrc"   %% "stub-data-generator" % "0.5.3"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-play-26"  % "0.42.0" % Test classifier "tests",
    "org.scalatest"          %% "scalatest"          % "3.0.8"  % "test",
    "com.typesafe.play"      %% "play-test"          % current  % "test",
    "org.pegdown"            % "pegdown"             % "1.6.0"  % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2"  % "test, it"
  )

}
