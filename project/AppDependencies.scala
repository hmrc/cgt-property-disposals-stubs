import sbt.*

object AppDependencies {
  val bootStrapVersion = "8.3.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30"  % bootStrapVersion,
    "uk.gov.hmrc"       %% "stub-data-generator"        % "1.1.0",
    "uk.gov.hmrc"       %% "tax-year"                   % "4.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootStrapVersion % Test
  )
}
