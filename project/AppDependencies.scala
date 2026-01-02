import sbt.*

object AppDependencies {
  val bootStrapVersion = "10.5.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-30" % bootStrapVersion,
    "uk.gov.hmrc" %% "stub-data-generator"       % "1.6.0",
    "uk.gov.hmrc" %% "tax-year"                  % "6.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootStrapVersion % Test
  )
}
