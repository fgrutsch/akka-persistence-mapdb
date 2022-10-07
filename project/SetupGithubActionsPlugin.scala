import sbt._
import sbt.Keys._
import sbtghactions.GenerativePlugin
import sbtghactions.GenerativePlugin.autoImport._
import sbtghactions.WorkflowStep._

object SetupGithubActionsPlugin extends AutoPlugin {

  override def requires: Plugins = GenerativePlugin
  override def trigger           = allRequirements
  override def buildSettings: Seq[Setting[_]] = Seq(
    githubWorkflowTargetTags ++= Seq("v*"),
    githubWorkflowJavaVersions += JavaSpec.temurin("17"),
    githubWorkflowBuild := Seq(
      WorkflowStep.Sbt(
        List("coverage", "test", "coverageReport", "coverageAggregate"),
        cond = Some(s"matrix.scala == '${crossScalaVersions.value.last}'")
      ),
      WorkflowStep.Sbt(
        List("codeVerify", "coverage", "test", "coverageReport", "coverageAggregate"),
        cond = Some(s"matrix.scala == '${crossScalaVersions.value.head}'")
      )
    ),
    githubWorkflowBuildPostamble += WorkflowStep.Use(
      UseRef.Public("codecov", "codecov-action", "v3"),
      cond = Some(s"matrix.scala == '${crossScalaVersions.value.head}'"),
      name = Some("Upload coverage to Codecov"),
      params = Map("fail_ci_if_error" -> "true")
    ),
    githubWorkflowPublish := Seq(WorkflowStep.Sbt(List("ci-release"))),
    githubWorkflowPublishTargetBranches += RefPredicate.StartsWith(Ref.Tag("v")),
    githubWorkflowPublish := Seq(
      WorkflowStep.Sbt(
        List("ci-release"),
        env = Map(
          "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
          "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
          "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
          "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
        )
      )
    ),
    githubWorkflowPublishPostamble ++= List(
      WorkflowStep.Run(
        List("sbt docs/paradox"),
        name = Some("Generate documentation"),
        cond = Some("startsWith(github.ref, 'refs/tags/v')")
      ),
      WorkflowStep.Use(
        UseRef.Public("JamesIves", "github-pages-deploy-action", "v4"),
        name = Some("Publish gh-pages"),
        cond = Some("startsWith(github.ref, 'refs/tags/v')"),
        params = Map("folder" -> "docs/target/paradox/site/main")
      )
    )
  )

}
