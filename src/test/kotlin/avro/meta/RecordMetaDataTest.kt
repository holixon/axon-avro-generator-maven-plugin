package io.holixon.avro.maven.avro.meta

import com.fasterxml.jackson.databind.ObjectMapper
import io.holixon.avro.maven.TestFixtures
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class RecordMetaDataTest {

  private val om: ObjectMapper = ObjectMapper()

  @Test
  internal fun `create recordMetaData from schema`() {
    val schema = TestFixtures.loadSchema("io.holixon.schema.bank.command", "CreateBankAccountCommand")

    val meta = RecordMetaData.parse(schema, om)
    assertThat(meta).isNotNull

    assertThat(meta.name).isEqualTo("CreateBankAccountCommand")
    assertThat(meta.namespace).isEqualTo("io.holixon.schema.bank.command")
    assertThat(meta.fullName).isEqualTo("io.holixon.schema.bank.command.CreateBankAccountCommand")
    assertThat(meta.revision).isEqualTo("47")
    assertThat(meta.type).isEqualTo(RecordMetaDataType.command)

  }
}
