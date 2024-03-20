import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings

val appName = "cgt-property-disposals-stubs"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    addCompilerPlugin(scalafixSemanticdb),
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test
  )
  .settings(CodeCoverageSettings.settings: _*)
  .settings(routesImport := Seq.empty)
  .settings(TwirlKeys.templateImports := Seq.empty)
  .settings(
    scalacOptions ++= Seq(
      "-Yrangepos",
      "-language:postfixOps"
    ),
    scalacOptions -= "-Xlint:nullary-override",
    Test / scalacOptions -= "-Ywarn-value-discard",
  )
  .settings(scalaVersion := "2.13.11")
  .settings(Compile / resourceDirectory := baseDirectory.value / "/conf")
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(PlayKeys.playDefaultPort := 7022)

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
