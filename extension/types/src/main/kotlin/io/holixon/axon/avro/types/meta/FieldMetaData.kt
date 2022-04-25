package io.holixon.axon.avro.types.meta

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.avro.Schema

data class FieldMetaData(
  val name: String,
  val type: FieldMetaDataType?
) {
  companion object {
    fun parse(schema: Schema, om: ObjectMapper = ObjectMapper()): List<FieldMetaData> = parse(schema.toString(), om)

    fun parse(schemaJson: String, om: ObjectMapper = ObjectMapper()): List<FieldMetaData> = om.readTree(schemaJson)
      .get("fields")
      .filter { it.get("meta") != null }
      .map {
        val name = requireNotNull(it.get("name")) { "name cannot be null in valid schema" }.asText()
        val type = it.get("meta")?.findValue("type")?.asText()

        FieldMetaData(name = name, type = FieldMetaDataType[type])
      }
  }
}
