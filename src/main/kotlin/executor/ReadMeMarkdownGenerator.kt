package io.holixon.avro.maven.executor

import io.holixon.avro.maven.avro.RecordMetaData
import io.holixon.avro.maven.avro.SchemaAndFile
import io.toolisticon.maven.fn.FileExt.readString
import io.toolisticon.maven.fn.FileExt.removeRoot
import io.toolisticon.maven.fn.FileExt.writeString
import mu.KLogger
import java.io.File

data class ReadMeMarkdownGenerator(
  val logger: KLogger,
  val enabled: Boolean,
  val projectBaseDir: File,
  val readmeFile: File,
  val schemaAndFiles: List<SchemaAndFile>,
  val markerBegin: String = DEFAULT_BEGIN,
  val markerEnd: String = DEFAULT_END
) : Runnable {
  companion object {
    const val N_A = "N/A"
    const val DEFAULT_BEGIN = "<!-- GENERATED AVSC DOCS (do not remove this marker) -->"
    const val DEFAULT_END = "<!-- /GENERATED AVSC DOCS  (do not remove this marker) -->"

    fun String.replaceBetweenMarkers(table: String, markerBegin: String = DEFAULT_BEGIN, markerEnd: String = DEFAULT_END): String {
      val buffer = java.lang.StringBuilder()
      var skipLine = false
      lines().forEach {
        if (!skipLine) {
          buffer.appendLine(it)
        }

        if (it.startsWith(markerBegin)) {
          skipLine = true
          buffer.appendLine()
          buffer.append(table)
        }
        if (it.startsWith(markerEnd)) {
          buffer.appendLine()
          buffer.appendLine()
          buffer.appendLine(markerEnd)
          skipLine = false
        }
      }

      return buffer.toString()
    }
  }

  data class TableRow(val type: String, val namespace: String, val name: String, val link: String, val revision: String, val description: String) {
    companion object {
      fun createRows(projectBaseDir: File, schemaAndFiles: List<SchemaAndFile>): List<TableRow> = schemaAndFiles.map {
        val meta = RecordMetaData(it.schema)
        TableRow(
          type = meta.type?.name ?: N_A,
          namespace = it.schema.namespace,
          name = it.schema.name,
          link = "./${it.file.removeRoot(projectBaseDir)}",
          revision = meta.revision ?: N_A,
          description = it.schema.doc ?: N_A
        )
      }

      fun renderTable(rows: List<TableRow>): String {
        val header = "| Type | Namespace | Name | Revision | Description |\n" +
          "|------|-----------|------|----------|-------------|\n"

        return header + rows.sortedBy { it.type }
          .sortedBy { it.name }.joinToString("\n") { it.render() }
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
