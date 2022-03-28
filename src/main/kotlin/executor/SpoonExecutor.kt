package io.holixon.avro.maven.executor

import io.holixon.avro.maven.maven.MojoComponents
import io.holixon.avro.maven.spoon.AxonRevisionAnnotationProcessor
import io.holixon.avro.maven.spoon.SpoonApiBuilder
import io.holixon.avro.maven.spoon.SpoonContext
import java.io.File

internal class SpoonExecutor(
  components: MojoComponents
) : AbstractExecutor(components) {

  private lateinit var _inputDirectory: File
  private lateinit var _outputDirectory: File

  override fun run() {
    val spoonContext = SpoonContext(log)

    val builder = SpoonApiBuilder()
      .isAutoImports(true)
      .noClasspath(false)
      .shouldCompile(false)
      .inputDirectory(_inputDirectory)
      .outputDirectory(_outputDirectory)
      .classPathElements(components.classpathElements)
      .processor(AxonRevisionAnnotationProcessor(spoonContext))

    val launcher = builder.build()
    launcher.run()
  }

  fun inputDirectory(inputDirectory: File) = apply {
    this._inputDirectory = inputDirectory
  }

  fun outputDirectory(outputDirectory: File) = apply {
    this._outputDirectory = outputDirectory
  }
}
