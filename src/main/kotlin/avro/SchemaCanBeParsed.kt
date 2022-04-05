package io.holixon.avro.maven.avro

import org.apache.avro.Schema
import java.io.File
import java.util.function.Function

class SchemaCanBeParsed(private val parser: Schema.Parser = Schema.Parser()) : Function<File, Result<Schema>> {

  override fun apply(avscFile: File): Result<Schema> = runCatching {
    require(avscFile.exists() && avscFile.isFile && avscFile.name.endsWith(".avsc")) { "Check only works on existing *.avsc files" }

    parser.parse(avscFile)
  }
}
