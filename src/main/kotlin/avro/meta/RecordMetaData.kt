package io.holixon.avro.maven.avro.meta

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.avro.Schema

data class RecordMetaData(
  val namespace: String,
  val name: String,
  val revision: String? = null,
  val type: RecordMetaDataType? = null
) {
  companion object {
    const val KEY = "meta"

    object KEYS {
      const val REVISION = "revision"
      const val TYPE = "type"
    }

    fun parse(schema: Schema, om: ObjectMapper = ObjectMapper()) = parse(schema.toString(), om)
    fun parse(schemaJson: String, om: ObjectMapper = ObjectMapper()): RecordMetaData {
      val json = om.readTree(schemaJson)

      val revision: String? = json.get(KEY)?.findPath(KEYS.REVISION)?.asText()
      val type: String? = json.get(KEY)?.findPath(KEYS.TYPE)?.asText()

      return RecordMetaData(
        name = requireNotNull(json.get("name")).asText(),
        namespace = requireNotNull(json.get("namespace")).asText(),
        revision = revision,
        type = RecordMetaDataType[type]
      )
    }
  }

  val fullName: String = "$namespace.$name"
}
