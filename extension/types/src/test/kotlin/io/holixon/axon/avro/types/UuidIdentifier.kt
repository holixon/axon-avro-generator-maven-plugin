package io.holixon.axon.avro.types

import org.apache.avro.Conversion
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import org.jmolecules.ddd.types.Identifier
import java.util.*

interface UuidIdentifier : Identifier {
  val name: String
  val uuid: UUID
}

data class UuidIdentifierData(override val name: String, override val uuid: UUID) : UuidIdentifier

class UuidIdentifierLogicalType(private val identifierName: String) : LogicalType(LOGICAL_TYPE_NAME) {
  companion object {
    const val LOGICAL_TYPE_NAME: String = "uuidIdentifier"
    const val NAME_PROP = "identifierName"
  }

  override fun addToSchema(schema: Schema?): Schema {
    return super.addToSchema(schema)
  }
}

class UuidIdentifierConversion : Conversion<UuidIdentifier>() {
  override fun getConvertedType(): Class<UuidIdentifier> = UuidIdentifier::class.java

  override fun getLogicalTypeName() = UuidIdentifierLogicalType.LOGICAL_TYPE_NAME
}
