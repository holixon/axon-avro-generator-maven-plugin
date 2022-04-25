package io.holixon.axon.avro.types.meta

import com.fasterxml.jackson.databind.ObjectMapper
import io.holixon.axon.avro.fixtures.schema.Fixtures
import org.apache.avro.Protocol
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.io.File
import java.net.URL


internal class RecordMetaDataTest {

  private val om: ObjectMapper = ObjectMapper()

  @Test
  fun `extract recordMetaData from schema`() {


    val schema = Fixtures.schemaCreateBankAccount

    val meta = RecordMetaData.parse(schema, om)
    assertThat(meta).isNotNull

    assertThat(meta.name).isEqualTo("CreateBankAccount")
    assertThat(meta.namespace).isEqualTo("io.holixon.axon.avro.fixtures.schema.command")
    assertThat(meta.fullName).isEqualTo("io.holixon.axon.avro.fixtures.schema.command.CreateBankAccount")
    assertThat(meta.revision).isEqualTo("1")
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
  fun `resolve type enums by name`(name:String?, expectedEnum: RecordMetaDataType?) {
    assertThat(RecordMetaDataType[name]).isEqualTo(expectedEnum)
  }
}
