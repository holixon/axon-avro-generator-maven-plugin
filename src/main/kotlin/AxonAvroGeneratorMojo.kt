package io.holixon.avro.maven

import io.holixon.avro.maven.AxonAvroGeneratorMojo.Companion.GOAL
import io.holixon.avro.maven.AxonAvroGeneratorMojoParameters.AxonAvroGeneratorMojoConfiguration
import io.holixon.avro.maven.executor.AvroSchemaExecutor
import io.holixon.avro.maven.executor.SpoonExecutor
import io.holixon.avro.maven.executor.UnpackDependencyExecutor
import io.holixon.avro.maven.maven.ParameterAwareMojo
import io.toolisticon.maven.io.FileExt.createIfNotExists
import io.toolisticon.maven.io.FileExt.subFolder
import io.toolisticon.maven.mojo.RuntimeScopeDependenciesConfigurator
import org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME
import java.io.File

/**
 * Mojo layer that gets paramers from pom configuration injected, analyses and transforms them and then
 * provides a type safe all-in-one configuration instance of type [AxonAvroGeneratorMojoConfiguration].
 */
abstract class AxonAvroGeneratorMojoParameters : ParameterAwareMojo<AxonAvroGeneratorMojoConfiguration>() {

  /**
   * List of schema FQN to contain in actual generation. Each schema has to be listed, otherwise
   * it will be ignored.
   */
  @Parameter(
    property = "includeSchemas",
    required = true,
    readonly = true
  )
  private var includeSchemas: List<String> = emptyList()

  /**
   * The base working directory. The mojo will create sub directories inside the working directory
   * as needed.
   */
  @Parameter(
    property = "workDirectory",
    required = true,
    defaultValue = "${AxonAvroGeneratorMojo.TARGET_DIRECTORY}/axon-avro-generator"
  )
  private lateinit var workDirectory: File

  /**
   * The output directory will contain the final generated sources.
   */
  @Parameter(
    property = "outputDirectory",
    required = true,
    defaultValue = "${AxonAvroGeneratorMojo.TARGET_DIRECTORY}/generated-sources/avro"
  )
  private lateinit var outputDirectory: File

  /**
   * The schema artifacts to download from a maven repository.
   * Schema artifacts are supposed to contain `avsc` avro schema files in their `src/main/resources`.
   */
  @Parameter(
    property = "schemaArtifacts",
    required = false,
    readonly = true
  )
  private var schemaArtifacts: List<String> = emptyList()

  /**
   * The schema artifacts to download from a maven repository.
   * Schema artifacts are supposed to contain `avsc` avro schema files in their `src/main/resources`.
   */
  @Parameter(
    property = "debug",
    required = false,
    readonly = true
  )
  private var debug: Boolean = false

  /**
   *
   */
  data class AxonAvroGeneratorMojoConfiguration(
    val debug: Boolean = false,
    /**
     * Final generated and spoon processed sources.
     */
    val outputDirectory: File,
    /**
     * Parent work dir for all relevant sub folders
     */
    val workDirectory: File,
    /**
     * Target of avsc schema files, either downloaded as schema-artifact or copied from src resources.
     */
    val schemaCollectionDir: File = workDirectory.subFolder("schemas"),
    /**
     * Intermediate folder used by avro generator plugin.
     */
    val avroGeneratedSourcesDir: File = workDirectory.subFolder("avro-generated"),
    val includeSchemas: LinkedHashSet<String>,
    val schemaArtifacts: LinkedHashSet<String>
  )

  override val configuration: AxonAvroGeneratorMojoConfiguration by lazy {
    require(this::workDirectory.isInitialized) { "the workDirectory is not configured." }
    require(this::outputDirectory.isInitialized) { "the outputDirectory is not configured." }

    require(this.schemaArtifacts.isNotEmpty()) {
      """missing configuration:
      |  <schemaArtifacts>
      |    <artifact>com.acme:schema-artifact:1.0</schemaArtifact>
      |    ...
      |  </schemaArtifacts>""".trimMargin()
    }
    require(this.includeSchemas.isNotEmpty()) {
      """missing configuration:
      |  <includeSchemas>
      |    <schema>com.acme.custom.GenericEvent</schema>
      |    ...
      |  </includeSchemas>""".trimMargin()
    }

    AxonAvroGeneratorMojoConfiguration(
      debug = debug,
      outputDirectory = outputDirectory.createIfNotExists(),
      workDirectory = workDirectory.createIfNotExists(),
      schemaArtifacts = linkedSetOf(*schemaArtifacts.toTypedArray()),
      includeSchemas = linkedSetOf(*includeSchemas.toTypedArray())
    )
  }
}

@Mojo(
  name = GOAL,
  defaultPhase = GENERATE_SOURCES,
  requiresDependencyResolution = COMPILE_PLUS_RUNTIME,
  configurator = RuntimeScopeDependenciesConfigurator.ROLE_HINT,
  requiresProject = true
)
class AxonAvroGeneratorMojo : AxonAvroGeneratorMojoParameters() {
  companion object {
    const val GOAL = "generate"
    const val TARGET_DIRECTORY = "\${project.build.directory}"
  }

  override fun execute() {

    if (configuration.debug) {
      logger.info { "comp: $components" }
      logger.info { "con: $configuration" }

      logger.error {  "artifactMap: ${components.project.artifactMap}" }

      components.project.artifacts.sortedBy { it.artifactId }
        .forEach { logger.error { " -  ${it.groupId}:::${it.artifactId}:::${it.version}   scope=${it.scope} " } }
    }

    logger.info { "--- downloading and unpacking schema artifacts" }
    UnpackDependencyExecutor(components)
      .outputDirectory(configuration.schemaCollectionDir)
      .schemaArtifacts(configuration.schemaArtifacts)
      .includeSchemas(configuration.includeSchemas)
      .run()

    logger.info { "--- generate classes from avro schema" }
    AvroSchemaExecutor(components)
      .inputDirectory(configuration.schemaCollectionDir)
      .outputDirectory(configuration.avroGeneratedSourcesDir)
      .run()


    logger.info { "--- spoon process generated sources" }
    SpoonExecutor(components)
      .inputDirectory(configuration.avroGeneratedSourcesDir)
      .outputDirectory(configuration.outputDirectory)
      .run()


    logger.info { "--- done processing" }
  }
}
