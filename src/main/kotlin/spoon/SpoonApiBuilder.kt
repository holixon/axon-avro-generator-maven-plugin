package io.holixon.avro.maven.spoon

import io.holixon.avro.maven.meta.RecordMetadata
import mu.KLogger
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificRecordBase
import spoon.Launcher
import spoon.SpoonAPI
import spoon.processing.Processor
import spoon.reflect.declaration.CtClass
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class SpoonContext(
  val logger: KLogger
) {

  private val schemas: MutableMap<CtClass<out SpecificRecordBase>, Schema> = ConcurrentHashMap<CtClass<out SpecificRecordBase>, Schema>()
  private val metaData: ConcurrentHashMap<CtClass<out SpecificRecordBase>, RecordMetadata> =
    ConcurrentHashMap<CtClass<out SpecificRecordBase>, RecordMetadata>()

  fun schema(type: CtClass<out SpecificRecordBase>) = schemas.computeIfAbsent(type) {
    // TODO: schema is a static method on the record class, so we do not need a newInstance here ... tbd

    val s = it.getField("SCHEMA\$").defaultExpression.toString()
      .removePrefix("new Schema.Parser().parse(\"")
      .removeSuffix("\")")
      .replace("""\"""", """"""")



    logger.info { "FIELD $s" }

    val schema = Schema.Parser().parse(s)
    logger.info { "schema: $schema" }
    schema
//    it.newInstance().schema
  }

  fun metaData(type: CtClass<out SpecificRecordBase>) = metaData.computeIfAbsent(type) {
    val schema = schema(it)

    RecordMetadata(schema)
  }
}

class SpoonApiBuilder {

  private val inputDirectories: MutableList<File> = mutableListOf()
  private var outputDirectory: File? = null
  private var shouldCompile: Boolean = false
  private var isAutoImports: Boolean = false
  private var noClasspath: Boolean = false
  private val classPathElements: MutableSet<String> = mutableSetOf()
  private val processors: MutableList<Processor<*>> = mutableListOf()

  fun inputDirectory(inputDirectoryPath: String) = apply {
    inputDirectory(File(inputDirectoryPath))
  }

  fun inputDirectory(inputDirectory: File) = apply {
    require(inputDirectory.exists() && inputDirectory.isDirectory) { "directory: $inputDirectory does not exist." }

    this.inputDirectories.add(inputDirectory)
  }

  fun outputDirectory(outputDirectory: File) = apply {
    this.outputDirectory = outputDirectory
    require(outputDirectory.exists() && outputDirectory.isDirectory) { "directory: $outputDirectory does not exist." }
  }

  fun outputDirectory(outputDirectoryPath: String) = apply {
    outputDirectory(File(outputDirectoryPath))
  }

  fun processor(processor: Processor<*>) = apply {
    this.processors.add(processor)
  }

  fun classPathElement(classPathElement: String) = apply {
    this.classPathElements.add(classPathElement)
  }

  fun classPathElements(classPathElements: Collection<String>) = apply {
    classPathElements.forEach { classPathElement(it) }
  }

  fun noClasspath(noClasspath: Boolean) = apply {
    this.noClasspath = noClasspath
  }

  fun shouldCompile(shouldCompile: Boolean) = apply {
    this.shouldCompile = shouldCompile
  }

  fun isAutoImports(isAutoImports: Boolean) = apply {
    this.isAutoImports = isAutoImports
  }

  fun build(): SpoonAPI {
    check()
    val launcher = Launcher()
    val env = launcher.environment
    env.isAutoImports = isAutoImports
    env.noClasspath = noClasspath
    env.setShouldCompile(shouldCompile)

    if (classPathElements.isNotEmpty()) {
      env.sourceClasspath = classPathElements.toTypedArray()
    }

    inputDirectories.forEach { launcher.addInputResource(it.path) }

    launcher.setSourceOutputDirectory(outputDirectory)

    processors.forEach { launcher.addProcessor(it) }

    return launcher
  }

  private fun check() {
    require(inputDirectories.isNotEmpty()) { "configure at least one input directory." }
    require(outputDirectory != null) { "outputDirectory must be set" }
  }

  override fun toString(): String {
    return "SpoonApiBuilder(" +
      "inputDirectories=$inputDirectories, " +
      "outputDirectory=$outputDirectory, " +
      "shouldCompile=$shouldCompile, " +
      "isAutoImports=$isAutoImports, " +
      "noClasspath=$noClasspath, " +
      "classPathElements=$classPathElements, " +
      "processors=$processors" +
      ")"
  }
}
