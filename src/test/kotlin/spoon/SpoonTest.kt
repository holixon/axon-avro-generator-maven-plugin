package io.holixon.avro.maven.spoon

import io.holixon.avro.maven.TestFixtures
import io.holixon.avro.maven.fn.HasRuntimeDependencyPredicate
import mu.toKLogger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import kotlin.io.path.isRegularFile
import kotlin.streams.asSequence

internal class SpoonTest {

  private val logger = LoggerFactory.getLogger(SpoonTest::class.java).toKLogger()

  @TempDir
  private lateinit var sourceDirectory: File

  @TempDir
  private lateinit var outputDirectory: File

  private val hasRuntimeDependencyPredicate = mock<HasRuntimeDependencyPredicate>()
    .apply { whenever(this.test(anyString(), anyString())).thenReturn(true) }

  @Test
  fun `create java file from string`() {
    val file = TestFixtures.createJavaFile(
      sourceDirectory,
      "io.holixon.schema.bank.event.BankAccountCreatedEvent.java",
      TestFixtures.generatedBankAccountCreatedEvent_java
    )

    val subPath = TestFixtures.subPath(sourceDirectory, file)

    assertThat(subPath).isEqualTo("io/holixon/schema/bank/event/BankAccountCreatedEvent.java")
  }

  @Test
  fun `add annotations to event`() {
    // GIVEN event java source in sourceDirectory
    TestFixtures.createJavaFile(
      sourceDirectory,
      "io.holixon.schema.bank.event.BankAccountCreatedEvent.java",
      TestFixtures.generatedBankAccountCreatedEvent_java
    )
    TestFixtures.createJavaFile(
      sourceDirectory,
      "io.holixon.schema.bank.command.CreateBankAccountCommand.java",
      TestFixtures.generatedCreateBankAccountCommand_java
    )


    val spoonContext = SpoonContext(logger, hasRuntimeDependencyPredicate)
    val spoon = SpoonApiBuilder()
      .inputDirectory(sourceDirectory)
      .outputDirectory(outputDirectory)
      .noClasspath(true)
      .isAutoImports(true)
      .processor(AxonRevisionAnnotationProcessor(spoonContext))
      .processor(JMoleculesValueObjectAnnotationProcessor(spoonContext))
      .processor(JMoleculesCommandAnnotationProcessor(spoonContext))
      .processor(JMoleculesDomainEventAnnotationProcessor(spoonContext))
      .build()

    // WHEN spoon runs
    spoon.run()

    val files = collectGeneratedJava()

    // THEN the processed source contains  annotation
    val eventSrc: String = requireNotNull(files["BankAccountCreatedEvent"])
    assertThat(eventSrc)
      .contains("""@Revision("1")""")
      .contains("""@ValueObject""")
      .contains("""@DomainEvent(name = "BankAccountCreatedEvent", namespace = "io.holixon.schema.bank.event")""")
  }

  @Test
  fun `add annotations to command`() {
    // GIVEN event java source in sourceDirectory
    TestFixtures.createJavaFile(
      sourceDirectory,
      "io.holixon.schema.bank.command.CreateBankAccountCommand.java",
      TestFixtures.generatedCreateBankAccountCommand_java
    )

    val spoonContext = SpoonContext(logger, hasRuntimeDependencyPredicate)
    val spoon = SpoonApiBuilder()
      .inputDirectory(sourceDirectory)
      .outputDirectory(outputDirectory)
      .noClasspath(true)
      .isAutoImports(true)
      .processor(AxonRevisionAnnotationProcessor(spoonContext))
      .processor(JMoleculesValueObjectAnnotationProcessor(spoonContext))
      .processor(JMoleculesCommandAnnotationProcessor(spoonContext))
      .processor(JMoleculesDomainEventAnnotationProcessor(spoonContext))
      .build()

    // WHEN spoon runs
    spoon.run()

    val files = collectGeneratedJava()


    // THEN the processed source contains  annotation
    val commandSrc: String = requireNotNull(files["CreateBankAccountCommand"])

    assertThat(commandSrc)
      .contains("""@Revision("47")""")
      .contains("""@ValueObject""")
      .contains("""@Command(name = "CreateBankAccountCommand", namespace = "io.holixon.schema.bank.command")""")
  }

  private fun collectGeneratedJava(): Map<String, String> {
    return Files.walk(outputDirectory.toPath())
      .asSequence()
      .filter { it.isRegularFile() }
      .map { it.toFile() }
      .filterNot { it.name == ".gitkeep" }
      .map { it.name.removeSuffix(".java") to Files.readString(it.toPath()) }
      .toMap()
  }
}
