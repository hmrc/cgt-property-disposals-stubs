import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-28"  % "5.12.0",
    "uk.gov.hmrc"       %% "stub-data-generator"        % "0.5.3",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.4",
    "com.github.java-json-tools" % "json-schema-validator" % "2.2.14"
  )

  val test = Seq(
    "org.scalatest" %% "scalatest" % "3.2.9" % "test",
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.36.8" % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0" % Test ,
    "org.pegdown"    % "pegdown"   % "1.6.0" % "test"
  )

}
