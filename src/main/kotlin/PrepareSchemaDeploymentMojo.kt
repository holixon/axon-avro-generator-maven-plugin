package io.holixon.avro.maven

import io.holixon.avro.maven.PrepareSchemaDeploymentMojo.Companion.GOAL
import io.holixon.avro.maven.avro.verifyAllAvscInRoot
import io.toolisticon.maven.fn.CleanDirectory
import io.toolisticon.maven.fn.FileExt.createIfNotExists
import io.toolisticon.maven.mojo.AbstractContextAwareMojo
import io.toolisticon.maven.mojo.RuntimeScopeDependenciesConfigurator
import io.toolisticon.maven.plugin.BuildHelperMavenPlugin
import io.toolisticon.maven.plugin.MavenResourcesPlugin
import io.toolisticon.maven.plugin.MavenResourcesPlugin.CopyResourcesCommand.CopyResource
import io.toolisticon.maven.plugin.ResourceData
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import java.io.File

@Mojo(
  name = GOAL,
  defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
  requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
  configurator = RuntimeScopeDependenciesConfigurator.ROLE_HINT,
  requiresProject = true
)
class PrepareSchemaDeploymentMojo : AbstractContextAwareMojo() {
  companion object {
    const val GOAL = "prepare-schema-deployment"
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
    defaultValue = "\${project.build.directory}/generated-resources/avro"
  )
  private lateinit var targetDirectory: File

  override fun execute() {
    // check avro schema dir exists
    require(this::sourceDirectory.isInitialized && sourceDirectory.isDirectory && sourceDirectory.exists()) { "Source directory '$sourceDirectory' has to be an existing directory" }

    // read and verify all avsc schema files in source directory
    val schemas = verifyAllAvscInRoot(sourceDirectory)

    // copy schema files to generated resources
    mojoContext.execute(
      MavenResourcesPlugin.CopyResourcesCommand(
        outputDirectory = targetDirectory.createIfNotExists(),
        resources = listOf(
          CopyResource(
            directory = sourceDirectory,
            filtering = false
          )
        )
      )
    )

    // remove .gitkeep and empty directories from generated-sources
    CleanDirectory(
      directory = targetDirectory,
      deleteFiles = setOf(".gitkeep"),
      deleteEmptyDirectories = true
    ).run()

    // add generated-source dir as resource directory, so it ends up in classes
    mojoContext.execute(
      BuildHelperMavenPlugin.AddResourceDirectoryCommand(
        resource = ResourceData(directory = targetDirectory)
      )
    )

    schemas.forEach {
      logger.info { it.fullName }
    }
  }
}
