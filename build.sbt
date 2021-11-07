ThisBuild / scalafixDependencies += Dependencies.organizeimports
ThisBuild / versionScheme := Some("early-semver")

addCommandAlias("codeFmt", ";headerCreate;scalafmtAll;scalafmtSbt;scalafixAll")
addCommandAlias("codeVerify", ";scalafmtCheckAll;scalafmtSbtCheck;scalafixAll --check;headerCheck")

lazy val commonSettings = Seq(
  organization           := "com.fgrutsch",
  sonatypeCredentialHost := "s01.oss.sonatype.org",
  sonatypeRepository     := "https://s01.oss.sonatype.org/service/local",
  sonatypeProfileName    := "com.fgrutsch",
  startYear              := Some(2021),
  homepage               := Some(url("https://github.com/fgrutsch/akka-persistence-mapdb")),
  licenses               := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  scmInfo := Some(
    ScmInfo(homepage.value.get, "scm:git:https://github.com/fgrutsch/akka-persistence-mapdb.git")
  ),
  developers += Developer(
    "contributors",
    "Contributors",
    "",
    url("https://github.com/fgrutsch/akka-persistence-mapdb/graphs/contributors")
  ),
  scalaVersion := "2.13.6",
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "utf-8",
    "-explaintypes",
    "-feature",
    "-language:higherKinds",
    "-unchecked",
    "-Xcheckinit",
    "-Xfatal-warnings",
    "-Wdead-code",
    "-Wunused:imports"
  ),
  headerLicense     := Some(HeaderLicense.ALv2("2021", "akka-persistence-mapdb contributors")),
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision
)

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(publish / skip := true)
  .aggregate(core, docs)

lazy val core = project
  .in(file("core"))
  .settings(commonSettings)
  .settings(
    name := "akka-persistence-mapdb",
    libraryDependencies ++= Dependencies.core,
    addCompilerPlugin(Dependencies.betterMonadicFor)
  )

lazy val docs = project
  .in(file("docs"))
  .settings(commonSettings)
  .settings(
    name           := "akka-persistence-mapdb-docs",
    publish / skip := true
  )
  .dependsOn(core)
  .enablePlugins(ParadoxSitePlugin)