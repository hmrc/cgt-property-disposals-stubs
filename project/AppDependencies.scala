import sbt.*

object AppDependencies {
  val bootStrapVersion = "8.4.0"
  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30"  % bootStrapVersion,
    "com.eclipsesource" %% "play-json-schema-validator" % "0.9.5"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "tax-year"               % "4.0.0",
    "uk.gov.hmrc"            %% "stub-data-generator"    % "1.1.0",
    "com.vladsch.flexmark"    % "flexmark-all"           % "0.36.8"         % "test",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0"          % Test,
    "org.scalatest"          %% "scalatest"              % "3.0.9"          % "test",
    "org.pegdown"             % "pegdown"                % "1.6.0"          % "test",
    "uk.gov.hmrc"            %% "bootstrap-test-play-30" % bootStrapVersion % Test
  )

}
