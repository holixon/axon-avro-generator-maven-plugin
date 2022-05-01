package io.holixon.axon.avro.types.meta

import com.fasterxml.jackson.databind.ObjectMapper
import io.holixon.axon.avro.fixtures.schema.Fixtures

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class FieldMetaDataTest {

  private val om: ObjectMapper = ObjectMapper()

  @Test
  fun `get fieldMetadata from accountId`() {

    val schema = Fixtures.schemaCreateBankAccount

    val meta = FieldMetaData.parse(schema, om)
    assertThat(meta).isNotEmpty.hasSize(1)

    with(meta[0]) {
      assertThat(name).isEqualTo("bankAccountId")
      assertThat(type).isEqualTo(FieldMetaDataType.IdentifierRef)
    }

  }


  @ParameterizedTest
  @CsvSource(value=[
    "identifierRef,IdentifierRef",
    ","
  ], nullValues = ["","null"])
  fun `resolve type enums by name`(name:String?, expectedEnum: FieldMetaDataType?) {
    assertThat(FieldMetaDataType[name]).isEqualTo(expectedEnum)
  }
}
