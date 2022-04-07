package io.holixon.avro.maven.avro.meta

enum class FieldMetaDataType {
  identifierRef,
  ;

  companion object {
    private val NAMES: Map<String, FieldMetaDataType> = FieldMetaDataType.values().associateBy { it.name }

    operator fun get(name:String?) : FieldMetaDataType? = name?.let { NAMES[it] }
  }
}
