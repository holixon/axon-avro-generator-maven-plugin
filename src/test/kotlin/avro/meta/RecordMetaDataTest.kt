package io.holixon.avro.maven.avro.meta

import com.fasterxml.jackson.databind.ObjectMapper
import io.holixon.avro.maven.TestFixtures
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


internal class RecordMetaDataTest {

  private val om: ObjectMapper = ObjectMapper()

  @Test
  fun `create recordMetaData from schema`() {
    val schema = TestFixtures.loadSchema("io.holixon.schema.bank.command", "CreateBankAccountCommand")

    val meta = RecordMetaData.parse(schema, om)
    assertThat(meta).isNotNull

    assertThat(meta.name).isEqualTo("CreateBankAccountCommand")
    assertThat(meta.namespace).isEqualTo("io.holixon.schema.bank.command")
    assertThat(meta.fullName).isEqualTo("io.holixon.schema.bank.command.CreateBankAccountCommand")
    assertThat(meta.revision).isEqualTo("47")
    assertThat(meta.type).isEqualTo(RecordMetaDataType.Command)

  }

  @ParameterizedTest
  @CsvSource(value=[
    "event,Event",
    "command,Command",
    "query,Query",
    "queryResult,QueryResult",
    ","
  ], nullValues = ["","null"])
  internal fun `resolve type enums by name`(name:String?, expectedEnum: RecordMetaDataType?) {
    assertThat(RecordMetaDataType[name]).isEqualTo(expectedEnum)
  }
}
