ThisBuild / scalafixDependencies += Dependencies.organizeImports
ThisBuild / versionScheme      := Some("early-semver")
ThisBuild / scalaVersion       := crossScalaVersions.value.last
ThisBuild / crossScalaVersions := Seq("2.13.7", "3.1.1-RC1")

ThisBuild / resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

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
  scalacOptions ++= {
    val common = Seq(
      "-deprecation",
      "-encoding",
      "utf-8",
      "-feature",
      "-language:higherKinds",
      "-unchecked"
      // "-Xfatal-warnings"
    )

    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) =>
        common ++ List(
          "-explain-types",
          "-Ysafe-init",
          "-source:3.0-migration"
        )
      case _ =>
        common ++ List(
          "-explaintypes",
          "-Xcheckinit",
          "-Wdead-code",
          "-Wunused:imports",
          "-Xfatal-warnings"
        )
    }
  },
  Test / parallelExecution := false,
  headerLicense            := Some(HeaderLicense.ALv2("2021", "akka-persistence-mapdb contributors")),
  semanticdbEnabled        := true,
  semanticdbVersion        := scalafixSemanticdb.revision
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
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) => Nil
        case _            => Seq(compilerPlugin(Dependencies.betterMonadicFor))
      }
    }
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
