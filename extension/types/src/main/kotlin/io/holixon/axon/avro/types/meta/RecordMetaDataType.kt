package io.holixon.axon.avro.types.meta

import java.util.*

enum class RecordMetaDataType {
  Event,
  Command,
  Query,
  QueryResult,
  ;

  val decapitalizedName = name.replaceFirstChar { c -> c.lowercase(Locale.getDefault()) }

  companion object {
    private val DECAPITALIZED_NAMES = values().associateBy { it.decapitalizedName }

    operator fun get(name:String?) : RecordMetaDataType? = name?.let { DECAPITALIZED_NAMES[it] }
  }
}
