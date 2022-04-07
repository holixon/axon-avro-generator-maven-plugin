package io.holixon.avro.maven.avro.meta

enum class RecordMetaDataType {
  event,
  command,
  query,
  queryResult,
  ;

  companion object {
    private val NAMES = values().associateBy { it.name }

    operator fun get(name:String?) : RecordMetaDataType? = name?.let { NAMES[it] }
  }
}
