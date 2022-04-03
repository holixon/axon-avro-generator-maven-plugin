package io.holixon.avro.maven

import io.holixon.avro.maven.PrepareAvroSchemasMojo.Companion.GOAL
import io.toolisticon.maven.command.CopyResourcesCommand
import io.toolisticon.maven.command.CopyResourcesCommand.CopyResource
import io.toolisticon.maven.io.FileExt.createIfNotExists
import io.toolisticon.maven.mojo.AbstractContextAwareMojo
import io.toolisticon.maven.mojo.RuntimeScopeDependenciesConfigurator
import org.apache.avro.Schema
import org.apache.maven.model.Dependency
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import java.io.File
import java.nio.file.Files
import kotlin.io.path.name

@Mojo(
  name = GOAL,
  defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
  requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
  configurator = RuntimeScopeDependenciesConfigurator.ROLE_HINT,
  requiresProject = true
)
class PrepareAvroSchemasMojo : AbstractContextAwareMojo() {
  companion object {
    const val GOAL = "prepare"
  }

  @Parameter(
    property = "sourceDirectory",
    defaultValue = "\${project.basedir}/src/main/avro",
    required = false,
    readonly = true
  )
  private lateinit var sourceDirectory: File


  @Parameter(
    property = "targetDirectory",
    required = true,
    readonly = true,
    defaultValue = "\${project.build.outputDirectory}"
  )
  private lateinit var targetDirectory: File

  override fun execute() {
    //require(mojoContext.hasRuntimeDependency("org.apache.avro", "avro")) { "to validate schemas we need to have avro on the classpath" }
    require(this::sourceDirectory.isInitialized && sourceDirectory.isDirectory && sourceDirectory.exists()) { "Source directory '$sourceDirectory' has to be an existing directory" }

    mojoContext.mavenSession.currentProject.dependencies.add(Dependency().apply {
      groupId = "org.apache.avro"
      artifactId = "avro"
      version = "1.11.0"
    })

    Files.walk(sourceDirectory.toPath()).forEach {
      logger.info {
        """

        $it

      """.trimIndent()
      }
    }

    val schemas = Files.walk(sourceDirectory.toPath()).filter { it.name.endsWith(".avsc") }
      .map {
        try {
          Schema.Parser().parse(it.toFile())
        } catch (e: Exception) {
          logger.error { "Error parsing ${it.name}: ${e.message}" }
          throw e
        }
      }

    schemas.forEach {
      logger.info { "${it.namespace}:${it.name}" }
    }

    mojoContext.execute(
      CopyResourcesCommand(
        outputDirectory = targetDirectory.createIfNotExists(),
        resources = listOf(
          CopyResource(
            directory = sourceDirectory,
            filtering = false
          )
        )
      )
    )

  }
}
