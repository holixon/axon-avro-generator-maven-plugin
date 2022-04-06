package io.holixon.avro.maven.executor

import io.holixon.avro.maven.fn.HasRuntimeDependencyPredicate
import io.holixon.avro.maven.spoon.*
import io.toolisticon.maven.MojoContext
import java.io.File

internal class SpoonExecutor(
  mojoContext: MojoContext
) : AbstractExecutor(mojoContext) {

  private lateinit var _inputDirectory: File
  private lateinit var _outputDirectory: File

  private val hasRuntimeDependencyPredicate = HasRuntimeDependencyPredicate(mojoContext.mavenProject)

  override fun run() {
    val spoonContext = SpoonContext(log, hasRuntimeDependencyPredicate)

    val builder = SpoonApiBuilder()
      .isAutoImports(true)
      .noClasspath(false)
      .shouldCompile(false)
      .inputDirectory(_inputDirectory)
      .outputDirectory(_outputDirectory)
      .classPathElements(components.classpathElements)

      .processor(AxonRevisionAnnotationProcessor(spoonContext))
      .processor(JMoleculesValueObjectAnnotationProcessor(spoonContext))
      .processor(JMoleculesCommandAnnotationProcessor(spoonContext))
      .processor(JMoleculesDomainEventAnnotationProcessor(spoonContext))

    builder.build().run()
  }

  fun inputDirectory(inputDirectory: File) = apply {
    this._inputDirectory = inputDirectory
  }

  fun outputDirectory(outputDirectory: File) = apply {
    this._outputDirectory = outputDirectory
  }
}
