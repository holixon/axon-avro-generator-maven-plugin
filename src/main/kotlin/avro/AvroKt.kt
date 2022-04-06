package io.holixon.avro.maven.avro

import org.apache.avro.AvroRuntimeException
import org.apache.avro.Schema
import java.io.File
import java.nio.file.Files
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.streams.toList


class CannotParseAvroSchemaException(msg: String, exception: Throwable) : AvroRuntimeException(msg, exception)

class AvroSchemaFqnMismatch(namespace: String, name: String, path: String) : AvroRuntimeException("schema fqn='$namespace.$name' did not match path='$path'")

data class AvroFqn(val namespace: String, val name: String)


fun fileToFqn(path: String): AvroFqn {
  val name = path.substringAfterLast("/").substringBeforeLast(".")
  val namespace = path.substringBeforeLast("/").replace("/", ".")

  return AvroFqn(namespace, name)
}


@Throws(CannotParseAvroSchemaException::class)
fun canParseSchema(avscFile: File, parser: Schema.Parser = Schema.Parser()): Schema {
  require(avscFile.exists() && avscFile.isFile && avscFile.name.endsWith(".avsc")) { "Check only works on existing *.avsc files" }

  return try {
    parser.parse(avscFile)
  } catch (e: Exception) {
    throw CannotParseAvroSchemaException("cannot parse avsc file: $avscFile", e)
  }
}

@Throws(AvroSchemaFqnMismatch::class)
fun verifyPathAndSchemaFqnMatches(rootDirectory: File, avscFile: File, parser: Schema.Parser = Schema.Parser()): Schema {
  val schema = canParseSchema(avscFile, parser)
  val subPath = avscFile.path.removePrefix("${rootDirectory.path}/")

  val fileFqn: AvroFqn = fileToFqn(subPath)

  if (fileFqn.namespace != schema.namespace || fileFqn.name != schema.name) {
    throw AvroSchemaFqnMismatch(schema.namespace, schema.name, subPath)
  }

  return schema
}

data class SchemaAndFile(val schema: Schema, val file: File, val sourceDirectory: File)

fun verifyAllAvscInRoot(rootDirectory: File, parser: Schema.Parser = Schema.Parser()): List<SchemaAndFile> = Files.walk(rootDirectory.toPath())
  .filter { it.isRegularFile() && it.name.endsWith(".avsc") }
  .map {
    SchemaAndFile(
      schema = verifyPathAndSchemaFqnMatches(rootDirectory, it.toFile(), parser),
      file = it.toFile(),
      sourceDirectory = rootDirectory
    )
  }
  .toList()


// TODO: schema is a static method on the record class, so we do not need a newInstance here ... tbd
fun schemaFieldToRecordMetadata(defaultExpression: String, parser: Schema.Parser = Schema.Parser()): Schema {

  val schemaString = defaultExpression
    .removePrefix("new Schema.Parser().parse(\"")
    .removeSuffix("\")")
    .replace("""\"""", """"""")

  return Schema.Parser().parse(schemaString)
}
