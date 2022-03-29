package io.holixon.avro.maven.spoon

import mu.toKLogger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isRegularFile
import kotlin.streams.asSequence

internal class SpoonTest {

  private val logger = LoggerFactory.getLogger(SpoonTest::class.java).toKLogger()

  @TempDir
  lateinit var outputDirectory: File

  @Test
  fun `verify that axon revision annotation is added to generated source`() {
    val projectDirAbsolutePath = Paths.get("").toAbsolutePath().toString()
    val resourcesPath: Path = Paths.get(projectDirAbsolutePath, "/src/test/resources/generated-sources/avro")
    Files.walk(resourcesPath)
      .filter { item -> Files.isRegularFile(item) }
      .filter { item -> item.toString().endsWith(".java") }
      .forEach { item -> println("filename: $item") }

    val spoonContext = SpoonContext(logger)
    val spoon = SpoonApiBuilder()
      .inputDirectory(resourcesPath.toFile())
      .outputDirectory(outputDirectory)
      .noClasspath(true)
      .isAutoImports(true)
      .processor(AxonRevisionAnnotationProcessor(spoonContext))
      .build()

    spoon.run()

    val map: Map<String, String> = Files.walk(outputDirectory.toPath())
      .asSequence()
      .filter { it.isRegularFile() }
      .map { it.toFile() }
      .filterNot { it.name == ".gitkeep" }
      .map { it.name.removeSuffix(".java") to Files.readString(it.toPath()) }
      .toMap()

    println(map)
    println(map.size)

    val eventSrc = map.get("BankAccountCreatedEvent")
    assertThat(eventSrc).contains("""@Revision("1")""")
  }
}
