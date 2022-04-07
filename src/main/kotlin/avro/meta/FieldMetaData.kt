package io.holixon.avro.maven.avro.meta

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.avro.Schema

data class FieldMetaData(
  val name: String,
  val type: FieldMetaDataType?
) {
  companion object {
    fun parse(schema: Schema, om: ObjectMapper = ObjectMapper()): List<FieldMetaData> = parse(schema.toString(), om)

    fun parse(schemaJson: String, om: ObjectMapper = ObjectMapper()): List<FieldMetaData> = om.readTree(schemaJson)
      .get("fields").map {
        val name = requireNotNull(it.get("name")) { "name cannot be null in valid schema" }.asText()
        val type = it.get("meta")?.findValue("type")?.asText()

        FieldMetaData(name = name, type = FieldMetaDataType[type])
      }
  }
}
