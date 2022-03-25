package io.holixon.avro.maven

import io.holixon.avro.maven.AxonAvroGeneratorMojo.Companion.GOAL
import io.holixon.avro.maven.executor.AvroSchemaExecutor
import io.holixon.avro.maven.executor.UnpackDependencyExecutor
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.BuildPluginManager
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment
import org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment

@Mojo(
  name = GOAL,
  defaultPhase = LifecyclePhase.GENERATE_SOURCES
)
class AxonAvroGeneratorMojo : AbstractMojo() {
  companion object {
    const val GOAL = "generate"
    const val TARGET_DIRECTORY = "\${project.build.directory}"
  }

  @Parameter(property = "project", required = true, readonly = true)
  private lateinit var project: MavenProject

  @Parameter(property = "session", required = true, readonly = true)
  private lateinit var session: MavenSession

  @Component
  private lateinit var buildPluginManager: BuildPluginManager

  @Parameter(property = "downloadDirectory", required = true, defaultValue = "$TARGET_DIRECTORY/downloaded-resources/avro/")
  private lateinit var downloadDirectory: String

  @Parameter(property = "generatedSourcesDirectory", required = true, defaultValue = "$TARGET_DIRECTORY/generated-sources/avro/")
  private lateinit var generatedSourcesDirectory: String

  @Parameter(property = "schemaArtifacts", required = false, readonly = true)
  private lateinit var schemaArtifacts : MutableList<String>

  @Parameter(property = "includeSchemas", required = true, readonly = true)
  private lateinit var includeSchemas : MutableList<String>

  private val environment: ExecutionEnvironment by lazy {
    executionEnvironment(project, session, buildPluginManager)
  }

  override fun execute() {
    require(this::schemaArtifacts.isInitialized) { """missing configuration:
      |  <schemaArtifacts>
      |    <artifact>com.acme:schema-artifact:1.0</schemaArtifact>
      |    ...
      |  </schemaArtifacts>""".trimMargin() }
    require(this::includeSchemas.isInitialized && this.includeSchemas.isNotEmpty()) { """missing configuration:
      |  <includeSchemas>
      |    <schema>com.acme.custom.GenericEvent</schema>
      |    ...
      |  </includeSchemas>""".trimMargin() }

    UnpackDependencyExecutor(environment)
      .addArtifactItems(schemaArtifacts)
      .outputDirectory(downloadDirectory)
      .includeSchemas(includeSchemas)
      .run()

    AvroSchemaExecutor(environment)
      .sourceDirectory(downloadDirectory)
      .outputDirectory(generatedSourcesDirectory)
      .run()
  }

//  <!-- GENERATE FROM AVRO SCHEMA -->
//  <includes>com/fiege/oms/customermanagement/event/CustomerCreatedEvent.avsc,com/fiege/oms/global/query/LookupCustomerQuery.avsc,com/fiege/oms/global/query/LookupCustomerQueryResult.avsc</includes>
//  <excludes>META-INF/**</excludes>
//</configuration>
//</execution>
//</executions>
//</plugin>
//

}
