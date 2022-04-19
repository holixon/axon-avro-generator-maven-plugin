package io.holixon.avro.maven.executor

import io.holixon.avro.maven.TestFixtures
import io.holixon.avro.maven.avro.verifyAllAvscInRoot
import io.holixon.avro.maven.executor.ReadMeMarkdownGenerator.Companion.replaceBetweenMarkers
import io.toolisticon.maven.fn.FileExt.append
import io.toolisticon.maven.fn.FileExt.createSubFoldersFromPath
import io.toolisticon.maven.fn.FileExt.readString
import io.toolisticon.maven.fn.FileExt.writeString
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import java.io.File


internal class ReadMeMarkdownGeneratorTest {

  private val logger = KLogging().logger

  @TempDir
  private lateinit var projectBaseDir: File

  private val sourceDirectory by lazy {
    projectBaseDir.createSubFoldersFromPath("src/main/avro")
  }

  @ParameterizedTest
  @EnumSource(ReplaceBetweenMarkersParameters::class)
  fun `replace between markers`(parameter: ReplaceBetweenMarkersParameters) {
    val replaced = parameter.orig.replaceBetweenMarkers(parameter.value, parameter.markerStart, parameter.markerEnd)

    assertThat(replaced).isEqualTo(parameter.expected)
  }

  enum class ReplaceBetweenMarkersParameters(
    val orig : String,
    val expected: String
  ) {
    NOOP_IF_MARKERS_ARE_MISSING(
      """
        some

        text
      """.trimIndent(),
      """
        some

        text
      """.trimIndent()
    ),
    NOOP_IF_START_MISSING(
      """
        some

        <!-- END -->
        text
      """.trimIndent(),
      """
        some

        <!-- END -->
        text
      """.trimIndent()
    ),
    NOOP_IF_END_MISSING(
      """
        some

        <!-- START -->
        text
      """.trimIndent(),
      """
        some

        <!-- START -->
        text
      """.trimIndent()
    ),
    NOOP_IF_START_AFTER_END(
      """
        <!-- END -->
        some

        <!-- START -->
        text
      """.trimIndent(),
      """
        <!-- END -->
        some

        <!-- START -->
        text
      """.trimIndent()
    ),
    REPLACE_IF_START_END(
      """
        before

        <!-- START -->
        some
        <!-- END -->

        after
      """.trimIndent(),
      """
        before

        <!-- START -->

        REPLACED

        <!-- END -->

        after
      """.trimIndent()
    ),
    ;

    val value: String = """

      REPLACED

    """.trimIndent()
    val markerStart: String = "<!-- START -->\n"
    val markerEnd: String = "<!-- END -->\n"
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
      <!-- START GENERATED AVSC DOCS (do not remove this marker) -->

      | Type | Namespace | Name | Revision | Description |
      |------|-----------|------|----------|-------------|
      | **event** | _io.holixon.schema.bank.event_ | [BalanceChangedEvent](./src/main/avro/io/holixon/schema/bank/event/BalanceChangedEvent.avsc) | 1 | Domain event containing accountId and new balance |

      <!-- END   GENERATED AVSC DOCS (do not remove this marker) -->

    """.trimIndent())
  }

  private val readmeContent = """
    # schema-global

    Global stuff

    <!-- START GENERATED AVSC DOCS (do not remove this marker) -->
    ...
    <!-- END   GENERATED AVSC DOCS (do not remove this marker) -->

    Footer stuff.

  """.trimIndent()
}
