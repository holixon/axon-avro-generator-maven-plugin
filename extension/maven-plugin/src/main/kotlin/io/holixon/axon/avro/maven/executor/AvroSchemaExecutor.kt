package io.holixon.axon.avro.maven.executor

import io.toolisticon.maven.MojoContext
import org.twdata.maven.mojoexecutor.MojoExecutor.element
import java.io.File

internal class AvroSchemaExecutor(
  components: MojoContext
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
