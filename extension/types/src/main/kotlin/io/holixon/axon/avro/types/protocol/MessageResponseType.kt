package io.holixon.axon.avro.types.protocol

import io.toolisticon.lib.avro.ext.SchemaExt.fqn
import io.toolisticon.lib.avro.fqn.SchemaFqn
import org.apache.avro.Protocol.Message
import org.apache.avro.Schema

val Message.messageResponseType: MessageResponseType
  get() = if (response.type == Schema.Type.ARRAY) {
    ListMessageResponseType(response.elementType.fqn())
  } else if (response.isUnion && response.isNullable) {
    val fqn = response.types.filter { it.type == Schema.Type.RECORD }
      .map { it.fqn() }
      .single()
    OptionalMessageResponseType(fqn)
  } else {
    SingleMessageResponseType(response.fqn())
  }

sealed interface MessageResponseType {
  val fqn: SchemaFqn
}

data class SingleMessageResponseType(override val fqn: SchemaFqn) : MessageResponseType

data class OptionalMessageResponseType(override val fqn: SchemaFqn) : MessageResponseType

data class ListMessageResponseType(override val fqn: SchemaFqn) : MessageResponseType
