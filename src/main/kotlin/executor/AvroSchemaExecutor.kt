package io.holixon.avro.maven.executor

import io.holixon.avro.maven.AxonAvroGeneratorMojoParameters.AxonAvroGeneratorMojoConfiguration
import io.holixon.avro.maven.maven.MojoComponents
import org.twdata.maven.mojoexecutor.MojoExecutor.element
import java.io.File

internal class AvroSchemaExecutor(
  components: MojoComponents
) : AbstractMojoExecutor(
  groupId = "org.apache.avro",
  artifactId = "avro-maven-plugin",
  version = "1.11.0",
  components = components
) {

  private lateinit var _inputDirectory: File
  private lateinit var _outputDirectory: File

  override fun run() = executeMojo(
    "schema",
    element("customConversions", "org.apache.avro.Conversions\$UUIDConversion"),
    element("stringType", "String"),
    element("createSetters", "false"),
    element("sourceDirectory", _inputDirectory.path),
    element("outputDirectory", _outputDirectory.path)
  )

  fun inputDirectory(inputDirectory: File) = apply {
    this._inputDirectory = inputDirectory
  }

  fun outputDirectory(outputDirectory: File) = apply {
    this._outputDirectory = outputDirectory
  }
}
