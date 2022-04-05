package io.holixon.avro.maven.spoon

import io.holixon.avro.maven.TestFixtures
import mu.toKLogger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.slf4j.LoggerFactory
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isRegularFile
import kotlin.streams.asSequence

internal class SpoonTest {

  private val logger = LoggerFactory.getLogger(SpoonTest::class.java).toKLogger()

  @TempDir
  private lateinit var sourceDirectory: File

  @TempDir
  private lateinit var outputDirectory: File

  @Test
  fun `create java file from string`() {
    val file = TestFixtures.createJavaFile(sourceDirectory, "io.holixon.schema.bank.event.BankAccountCreatedEvent.java", TestFixtures.generatedBankAccountCreatedEvent_java)

    val subPath = TestFixtures.subPath(sourceDirectory, file)

    assertThat(subPath).isEqualTo("io/holixon/schema/bank/event/BankAccountCreatedEvent.java")
  }

  @Test
  fun `verify that axon revision annotation is added to generated source`() {
    val file = TestFixtures.createJavaFile(sourceDirectory, "io.holixon.schema.bank.event.BankAccountCreatedEvent.java", TestFixtures.generatedBankAccountCreatedEvent_java)

    val spoonContext = SpoonContext(logger)
    val spoon = SpoonApiBuilder()
      .inputDirectory(sourceDirectory)
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
