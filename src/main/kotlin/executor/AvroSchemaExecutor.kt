package io.holixon.avro.maven.executor

import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment
import org.twdata.maven.mojoexecutor.MojoExecutor.element

internal class AvroSchemaExecutor(environment: ExecutionEnvironment) : AbstractMojoExecutor(
  groupId = "org.apache.avro",
  artifactId = "avro-maven-plugin",
  version = "1.11.0",
  environment
) {

  private lateinit var sourceDirectory: String
  private lateinit var outputDirectory: String

  fun sourceDirectory(sourceDirectory: String) = apply { this.sourceDirectory = sourceDirectory }
  fun outputDirectory(outputDirectory: String) = apply { this.outputDirectory = outputDirectory }

  override fun run() = executeMojo(
    "schema",
    element("customConversions", "org.apache.avro.Conversions\$UUIDConversion"),
    element("stringType", "String"),
    element("createSetters", "false"),
    element("sourceDirectory", sourceDirectory),
    element("outputDirectory", outputDirectory)
  )
}
