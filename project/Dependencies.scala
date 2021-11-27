import sbt._

object Dependencies {

  object Versions {
    val akka = "2.6.17"
  }

  val core: Seq[ModuleID] = Seq(
    ("com.typesafe.akka" %% "akka-persistence"           % Versions.akka).cross(CrossVersion.for3Use2_13),
    ("com.typesafe.akka" %% "akka-persistence-query"     % Versions.akka).cross(CrossVersion.for3Use2_13),
    ("com.typesafe.akka" %% "akka-slf4j"                 % Versions.akka).cross(CrossVersion.for3Use2_13),
    ("com.typesafe.akka" %% "akka-stream-testkit"        % Versions.akka % Test).cross(CrossVersion.for3Use2_13),
    ("com.typesafe.akka" %% "akka-persistence-tck"       % Versions.akka % Test).cross(CrossVersion.for3Use2_13),
    ("com.typesafe.akka" %% "akka-serialization-jackson" % Versions.akka % Test).cross(CrossVersion.for3Use2_13),
    "org.mapdb"           % "mapdb"                      % "3.0.8",
    ("org.scalatest"      %% "scalatest"                  % "3.2.10"      % Test).cross(CrossVersion.for3Use2_13),
    "ch.qos.logback"      % "logback-classic"            % "1.2.7"       % Test
  )

  val betterMonadicFor: ModuleID = "com.olegpy"           %% "better-monadic-for" % "0.3.1"
  val organizeImports: ModuleID  = "com.github.liancheng" %% "organize-imports"   % "0.6.0"

}
