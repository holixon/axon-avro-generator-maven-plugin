package io.holixon.avro.maven.avro.meta

import com.fasterxml.jackson.databind.ObjectMapper
import io.holixon.avro.maven.TestFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class FieldMetaDataTest {

  private val om: ObjectMapper = ObjectMapper()

  @Test
  fun `get fieldMetadata from accountId`() {
    val schema = TestFixtures.loadSchema("io.holixon.schema.bank.command", "CreateBankAccountCommand")

    val meta = FieldMetaData.parse(schema, om)
    assertThat(meta).isNotEmpty.hasSize(1)

    with(meta[0]) {
      assertThat(name).isEqualTo("id")
      assertThat(type).isEqualTo(FieldMetaDataType.identifierRef)
    }
  }
}
