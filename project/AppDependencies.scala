import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-28"  % "5.25.0",
    "uk.gov.hmrc"       %% "stub-data-generator"        % "0.5.3",
    "com.eclipsesource" %% "play-json-schema-validator" % "0.9.5"
  )

  val test = Seq(
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.36.8" % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0" % Test ,
    "org.scalatest" %% "scalatest" % "3.0.9" % "test",
    "org.pegdown"    % "pegdown"   % "1.6.0" % "test"
  )

}
