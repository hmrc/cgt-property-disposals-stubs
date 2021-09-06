resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)
resolvers += Resolver.jcenterRepo
resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"
resolvers += "HMRC Releases" at "https://artefacts.tax.service.gov.uk/artifactory/hmrc-releases/"
resolvers += "jitpack.io" at "https://jitpack.io"

addSbtPlugin("uk.gov.hmrc"                % "sbt-auto-build"      % "3.0.0")
addSbtPlugin("uk.gov.hmrc"                % "sbt-distributables"  % "2.1.0")
addSbtPlugin("com.typesafe.play"          % "sbt-plugin"          % "2.8.7")
addSbtPlugin("org.scalameta"              % "sbt-scalafmt"        % "2.4.0")
addSbtPlugin("ch.epfl.scala"              % "sbt-scalafix"        % "latest.integration")
addSbtPlugin("io.github.davidgregory084"  % "sbt-tpolecat"        % "0.1.12")
