package io.holixon.axon.avro.maven.executor

import io.holixon.axon.avro.types.meta.RecordMetaData
import io.toolisticon.lib.avro.declaration.SchemaDeclaration
import io.toolisticon.maven.fn.FileExt.readString
import io.toolisticon.maven.fn.FileExt.writeString
import mu.KLogger
import java.io.File

data class ReadMeMarkdownGenerator(
  val logger: KLogger,
  val enabled: Boolean,
  val projectBaseDir: File,
  val readmeFile: File,
  val schemaAndFiles: List<SchemaDeclaration>,
  val markerBegin: String = DEFAULT_START,
  val markerEnd: String = DEFAULT_END
) : Runnable {
  companion object {
    const val N_A = "N/A"
    const val DEFAULT_START = "<!-- START GENERATED AVSC DOCS (do not remove this marker) -->\n"
    const val DEFAULT_END = "<!-- END   GENERATED AVSC DOCS (do not remove this marker) -->\n"

    /**
     * Inserts given string "table" between the two markers.
     * Noop if the markers are not present or in wrong order.
     */
    fun String.replaceBetweenMarkers(table: String, markerStart: String = DEFAULT_START, markerEnd: String = DEFAULT_END): String {
      val startIndex = indexOf(markerStart)
      val endIndex = indexOf(markerEnd)

      if (startIndex < 0 || endIndex < 0 || endIndex < startIndex) {
        return this
      }

      return java.lang.StringBuilder(substring(0, startIndex + markerStart.length))
        .append(table)
        .appendLine()
        .append(substring(endIndex))
        .toString()
    }
  }

  data class TableRow(val type: String, val namespace: String, val name: String, val link: String, val revision: String, val description: String) {
    companion object {
      fun createRows(projectBaseDir: File, schemaAndFiles: List<SchemaDeclaration>): List<TableRow> = schemaAndFiles.map {
        val meta = RecordMetaData.parse(it.content)
        TableRow(
          type = meta.type?.decapitalizedName ?: N_A,
          namespace = it.namespace,
          name = it.name,
          link = "./src/main/avro/${it.location.path}",
          revision = meta.revision ?: N_A,
          description = it.content.doc ?: N_A
        )
      }

      fun renderTable(rows: List<TableRow>): String {
        val header = "| Type | Namespace | Name | Revision | Description |\n" +
          "|------|-----------|------|----------|-------------|\n"

        val table = header + rows.sortedBy { it.type }
          .sortedBy { it.name }.joinToString("\n") { it.render() }
        return "\n$table\n"

      }
    }

    fun render() = """| **$type** | _${namespace}_ | [$name]($link) | $revision | $description |"""

  }

  override fun run() {
    if (!enabled) {
      return
    }

    val rows = TableRow.createRows(projectBaseDir, schemaAndFiles)

    val table = TableRow.renderTable(rows)

    logger.info { "adding table: \n$table" }

    val newReadmeContent = readmeFile.readString().replaceBetweenMarkers(table, markerBegin, markerEnd)

    readmeFile.writeString(newReadmeContent)
  }
}
