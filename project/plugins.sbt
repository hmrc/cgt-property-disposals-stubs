resolvers += "HMRC-open-artefacts-maven" at "https://open.artefacts.tax.service.gov.uk/maven2"
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)
resolvers += Resolver.jcenterRepo
resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("uk.gov.hmrc"                % "sbt-auto-build"      % "3.20.0")
addSbtPlugin("uk.gov.hmrc"                % "sbt-distributables"  % "2.2.0")
addSbtPlugin("com.typesafe.play"          % "sbt-plugin"          % "2.8.20")
addSbtPlugin("org.scoverage"              % "sbt-scoverage"       % "2.0.10")
addSbtPlugin("org.scalameta"              % "sbt-scalafmt"        % "2.4.0")
addSbtPlugin("ch.epfl.scala"              % "sbt-scalafix"        % "latest.integration")
addSbtPlugin("io.github.davidgregory084"  % "sbt-tpolecat"        % "0.1.12")

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always