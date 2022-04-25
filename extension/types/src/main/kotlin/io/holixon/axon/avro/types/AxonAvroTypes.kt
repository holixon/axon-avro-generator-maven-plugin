package io.holixon.axon.avro.types

import org.apache.avro.Protocol
import org.apache.avro.Schema

object AxonAvroTypes {

  data class SchemaFqn(val namespace: String, val name: String)

  val Schema.fqn : SchemaFqn get() = SchemaFqn(this.namespace, this.name)

  fun Protocol.message(name:String) = requireNotNull(this.messages[name]) {"No protocol message with name '$name' found."}

}
