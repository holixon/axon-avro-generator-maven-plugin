package io.holixon.avro.maven.maven

import io.toolisticon.maven.KotlinMojoHelper
import mu.KLogger
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.BuildPluginManager
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject

/**
 * Root class for AbstractMojo. This gets the relevant components injected.
 *
 * * [MavenProject]
 * * [MavenSession]
 * * [BuildPluginManager]
 *
 * Provides:
 *
 * * [KLogger] - using the maven [Log].
 * * [MojoComponents]
 */
abstract class ComponentAwareMojo : AbstractMojo() {

  protected val logger: KLogger = KotlinMojoHelper.logger(this)

  @Parameter(defaultValue = "\${project}", readonly = true)
  private lateinit var project: MavenProject

  @Parameter(defaultValue = "\${session}", readonly = true)
  private lateinit var session: MavenSession

  @Component
  private lateinit var buildPluginManager: BuildPluginManager

  val components by lazy {
    MojoComponents(
      logger = logger,
      project = project,
      session = session,
      buildPluginManager = buildPluginManager
    )
  }
}
