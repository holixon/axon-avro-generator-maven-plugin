package io.holixon.avro.maven.executor

import io.holixon.avro.maven.TestFixtures
import io.holixon.avro.maven.avro.verifyAllAvscInRoot
import io.toolisticon.maven.fn.FileExt.append
import io.toolisticon.maven.fn.FileExt.createSubFoldersFromPath
import io.toolisticon.maven.fn.FileExt.readString
import io.toolisticon.maven.fn.FileExt.writeString
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File


internal class ReadMeMarkdownGeneratorTest {

  private val logger = KLogging().logger

  @TempDir
  private lateinit var projectBaseDir: File

  private val sourceDirectory by lazy {
    projectBaseDir.createSubFoldersFromPath("src/main/avro")
  }

  @Test
  fun `create rows`() {
    TestFixtures.createAvscFile(sourceDirectory, TestFixtures.balanceChangedEventAvsc)
    val readmeFile = projectBaseDir.append("README.md").writeString(readmeContent)

    val files = verifyAllAvscInRoot(sourceDirectory)

    ReadMeMarkdownGenerator(
      logger = logger, enabled = true, projectBaseDir = projectBaseDir,
      readmeFile = readmeFile, schemaAndFiles = files
    ).run()

    val newReadMe = readmeFile.readString()

    assertThat(newReadMe).contains("""
      <!-- GENERATED AVSC DOCS (do not remove this marker) -->

      | Type | Namespace | Name | Revision | Description |
      |------|-----------|------|----------|-------------|
      | **event** | _io.holixon.schema.bank.event_ | [BalanceChangedEvent](./src/main/avro/io/holixon/schema/bank/event/BalanceChangedEvent.avsc) | 1 | Domain event containing accountId and new balance |

      <!-- /GENERATED AVSC DOCS  (do not remove this marker) -->
    """.trimIndent())
  }

  private val readmeContent = """
    # schema-global

    Global stuff

    <!-- GENERATED AVSC DOCS (do not remove this marker) -->
    ...
    <!-- /GENERATED AVSC DOCS  (do not remove this marker) -->

    Footer stuff.

  """.trimIndent()
}
