package io.holixon.avro.maven.meta

import org.apache.avro.Schema

data class RecordMetadata(
  val namespace: String,
  val name: String,
  val fullName: String,
  val revision: String?,
  val type: DDDType?
) {
  companion object {
    const val META = "meta"

    object KEYS {
      const val REVISION = "revision"
      const val TYPE = "type"
    }

    operator fun invoke(schema: Schema): RecordMetadata {

      @Suppress("UNCHECKED_CAST")
      val meta: Map<String, Any> = if (schema.objectProps.containsKey(META)) {
        schema.objectProps[META] as Map<String, Any>
      } else {
        emptyMap()
      }

      return RecordMetadata(
        namespace = schema.namespace,
        name = schema.name,
        fullName = schema.fullName,
        revision = meta[KEYS.REVISION]?.let { it as String },
        type = meta[KEYS.TYPE]?.let { it as String }?.let(DDDType::valueOf)
      )
    }
  }


}

enum class DDDType {
  event, command, query, queryResult
}
