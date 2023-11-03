package io.holixon.axon.avro.maven.avro

import io.toolisticon.lib.avro.AvroKotlinLib
import io.toolisticon.lib.avro.AvroKotlinLib.EXTENSION_SCHEMA
import io.toolisticon.lib.avro.AvroKotlinLib.verifyPackagePathConvention
import io.toolisticon.lib.avro.declaration.SchemaDeclaration
import io.toolisticon.lib.avro.fqn.AvroDeclarationFqn
import io.toolisticon.lib.avro.fqn.AvroDeclarationMismatchException
import io.toolisticon.lib.avro.fqn.GenericAvroDeclarationFqn
import org.apache.avro.AvroRuntimeException
import org.apache.avro.Schema
import java.io.File
import kotlin.io.path.extension


class CannotParseAvroSchemaException(msg: String, exception: Throwable) : AvroRuntimeException(msg, exception)


@Throws(CannotParseAvroSchemaException::class)
fun canParseSchema(avscFile: File, parser: Schema.Parser = Schema.Parser()): Schema {
  require(avscFile.exists() && avscFile.isFile && avscFile.name.endsWith(EXTENSION_SCHEMA)) { "Check only works on existing *.avsc files" }

  return try {
    parser.parse(avscFile)
  } catch (e: Exception) {
    throw CannotParseAvroSchemaException("cannot parse avsc file: $avscFile", e)
  }
}


fun verifyAllAvscInRoot(rootDirectory: File, parser: Schema.Parser = Schema.Parser()): List<SchemaDeclaration> {
  fun generic(sd: AvroDeclarationFqn) : GenericAvroDeclarationFqn = GenericAvroDeclarationFqn(
    sd, sd.fileExtension
  )

  return AvroKotlinLib.findDeclarations(rootDirectory.toPath()) { EXTENSION_SCHEMA == it.extension }
    .map { it as SchemaDeclaration }
    .map {
      AvroKotlinLib.verifyPackagePathConvention(generic(it.location), generic(it.contentFqn))
      it
    }

}


// TODO: schema is a static method on the record class, so we do not need a newInstance here ... tbd
fun schemaFieldToRecordMetadata(defaultExpression: String, parser: Schema.Parser = Schema.Parser()): Schema {

  val schemaString = defaultExpression
    .removePrefix("new Schema.Parser().parse(\"")
    .removeSuffix("\")")
    .replace("""\"""", """"""")

  return Schema.Parser().parse(schemaString)
}
