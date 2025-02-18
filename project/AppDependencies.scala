import sbt.*

object AppDependencies {
  val bootStrapVersion = "9.6.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-30" % bootStrapVersion,
    "uk.gov.hmrc" %% "stub-data-generator"       % "1.2.0",
    "uk.gov.hmrc" %% "tax-year"                  % "5.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "stub-data-generator"    % "1.2.0"          % Test,
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootStrapVersion % Test
  )
}
