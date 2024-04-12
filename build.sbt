val appName = "cgt-property-disposals-stubs"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions ++= Seq("-Wconf:src=routes/.*:s")
  )
  .settings(CodeCoverageSettings.settings *)
  .settings(scalaVersion := "2.13.12")
  .settings(PlayKeys.playDefaultPort := 7022)
