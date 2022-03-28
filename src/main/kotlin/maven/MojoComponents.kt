package io.holixon.avro.maven.maven

import mu.KLogger
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.BuildPluginManager
import org.apache.maven.project.MavenProject
import org.twdata.maven.mojoexecutor.MojoExecutor
import kotlin.streams.asSequence

data class MojoComponents(
  val logger: KLogger,
  val project: MavenProject,
  val session: MavenSession,
  val buildPluginManager: BuildPluginManager
) {

  val environment: MojoExecutor.ExecutionEnvironment by lazy {
    MojoExecutor.executionEnvironment(project, session, buildPluginManager)
  }

  val classpathElements: Set<String> by lazy {
    project
      .compileClasspathElements
      .stream()
      .asSequence()
      .filterNot { project.build.outputDirectory == it } // TODO copied from camunda-swagger, why is that?
      .sortedBy { it.substringAfterLast("/") }
      .toSet()
  }

  override fun toString() = """
    MojoContext(
        logger=${logger.name},
        project=${project.groupId}.${project.artifactId},
        session=$session,
        buildPluginManager=$buildPluginManager,
        classpathElements=$classpathElements
  """.trimIndent()

}
