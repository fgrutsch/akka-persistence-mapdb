import sbt._

object Dependencies {

  object Versions {
    val akka = "2.6.17"
  }

  val core: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-persistence-typed" % Versions.akka,
    "com.typesafe.akka" %% "akka-persistence-tck"   % Versions.akka % Test,
    "org.mapdb"          % "mapdb"                  % "3.0.8",
    "org.scalatest"     %% "scalatest"              % "3.2.10"      % Test,
    "ch.qos.logback"     % "logback-classic"        % "1.2.6"       % Test
  )

  val betterMonadicFor: ModuleID = "com.olegpy"           %% "better-monadic-for" % "0.3.1"
  val organizeimports: ModuleID  = "com.github.liancheng" %% "organize-imports"   % "0.6.0"

}
