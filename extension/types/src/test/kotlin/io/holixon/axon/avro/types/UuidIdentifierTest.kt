package io.holixon.axon.avro.types

import org.apache.avro.LogicalTypes
import org.apache.avro.LogicalTypes.Decimal
import org.apache.avro.Schema
import org.junit.jupiter.api.Test

internal class UuidIdentifierTest {

  @Test
  fun `can use conversion from to identifier`() {
    val smallerSchema = Schema.createFixed("smallFixed", null, null, 3);
    smallerSchema.addProp("logicalType", "decimal");
    smallerSchema.addProp("precision", 5);
    smallerSchema.addProp("scale", 2);
    val smallerLogicalType = LogicalTypes.fromSchema(smallerSchema) as Decimal;

    println(smallerSchema)
    println(smallerLogicalType.precision)

  }
}
