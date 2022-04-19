package io.holixon.avro.maven.avro.meta

import java.util.*

enum class FieldMetaDataType {
  IdentifierRef,
  ;

  val decapitalizedName = name.replaceFirstChar { c -> c.lowercase(Locale.getDefault()) }


  companion object {
    private val NAMES: Map<String, FieldMetaDataType> = values().associateBy { it.decapitalizedName }

    operator fun get(name: String?): FieldMetaDataType? = name?.let { NAMES[it] }
  }
}
