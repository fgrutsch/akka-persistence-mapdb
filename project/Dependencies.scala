import sbt._

object Dependencies {

  object Versions {
    val akka = "2.6.20"
  }

  val core: Seq[ModuleID] = Seq(
    "ch.qos.logback"     % "logback-classic"            % "1.4.3"       % Test,
    "com.typesafe.akka" %% "akka-persistence"           % Versions.akka,
    "com.typesafe.akka" %% "akka-persistence-query"     % Versions.akka,
    "com.typesafe.akka" %% "akka-slf4j"                 % Versions.akka,
    "com.typesafe.akka" %% "akka-stream-testkit"        % Versions.akka % Test,
    "com.typesafe.akka" %% "akka-persistence-tck"       % Versions.akka % Test,
    "com.typesafe.akka" %% "akka-serialization-jackson" % Versions.akka % Test,
    "org.mapdb"          % "mapdb"                      % "3.0.8",
    "org.scalatest"     %% "scalatest"                  % "3.2.14"      % Test
  )

  val betterMonadicFor: ModuleID = "com.olegpy"           %% "better-monadic-for" % "0.3.1"
  val organizeImports: ModuleID  = "com.github.liancheng" %% "organize-imports"   % "0.6.0"

}
