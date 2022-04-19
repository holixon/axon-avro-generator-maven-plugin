package io.holixon.avro.maven.avro.meta

import com.fasterxml.jackson.databind.ObjectMapper
import io.holixon.avro.maven.TestFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class FieldMetaDataTest {

  private val om: ObjectMapper = ObjectMapper()

  @Test
  fun `get fieldMetadata from accountId`() {
    val schema = TestFixtures.loadSchema("io.holixon.schema.bank.command", "CreateBankAccountCommand")

    val meta = FieldMetaData.parse(schema, om)
    assertThat(meta).isNotEmpty.hasSize(1)

    with(meta[0]) {
      assertThat(name).isEqualTo("id")
      assertThat(type).isEqualTo(FieldMetaDataType.IdentifierRef)
    }
  }


  @ParameterizedTest
  @CsvSource(value=[
    "identifierRef,IdentifierRef",
    ","
  ], nullValues = ["","null"])
  internal fun `resolve type enums by name`(name:String?, expectedEnum: FieldMetaDataType?) {
    assertThat(FieldMetaDataType[name]).isEqualTo(expectedEnum)
  }
}
