import sbt._

object Dependencies {

  val core: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.10" % Test
  )

  val betterMonadicFor: ModuleID = "com.olegpy"           %% "better-monadic-for" % "0.3.1"
  val organizeimports: ModuleID  = "com.github.liancheng" %% "organize-imports"   % "0.5.0"

}
