import sbt._

object Dependencies {

  object Versions {
    val akka = "2.6.18"
  }

  val core: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-persistence"           % Versions.akka,
    "com.typesafe.akka" %% "akka-persistence-query"     % Versions.akka,
    "com.typesafe.akka" %% "akka-slf4j"                 % Versions.akka,
    "com.typesafe.akka" %% "akka-stream-testkit"        % Versions.akka % Test,
    "com.typesafe.akka" %% "akka-persistence-tck"       % Versions.akka % Test,
    "com.typesafe.akka" %% "akka-serialization-jackson" % Versions.akka % Test,
    "org.mapdb"          % "mapdb"                      % "3.0.8",
    "org.scalatest"     %% "scalatest"                  % "3.2.10"      % Test,
    "ch.qos.logback"     % "logback-classic"            % "1.2.10"      % Test,
    // For scala 3 cross build (https://github.com/akka/akka/commit/6c9c7338962cba5af98620742c4b4596d67b0b9e)
    ("com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.11.4"      % Test).cross(CrossVersion.for3Use2_13),
    "com.typesafe.akka"             %% "akka-remote"          % Versions.akka % Test
  )

  val betterMonadicFor: ModuleID = "com.olegpy"           %% "better-monadic-for" % "0.3.1"
  val organizeImports: ModuleID  = "com.github.liancheng" %% "organize-imports"   % "0.6.0"

}
