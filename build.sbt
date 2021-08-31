import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "cgt-property-disposals-stubs"

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    addCompilerPlugin(scalafixSemanticdb),
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test
  )
  .settings(routesImport := Seq.empty)
  .settings(TwirlKeys.templateImports := Seq.empty)
  .settings(scalacOptions ++= Seq(
      "-Yrangepos",
      "-language:postfixOps"
    ),
    Test / scalacOptions --= Seq("-Ywarn-value-discard")
  )
  .settings(scalaVersion := "2.12.13")
  .settings(publishingSettings: _*)
  .settings(Compile / resourceDirectory := baseDirectory.value / "/conf")
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(resolvers += MavenRepository("jitpack.io","https://jitpack.io"))
  .settings(PlayKeys.playDefaultPort := 7022)
