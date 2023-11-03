package io.holixon.axon.avro.types.meta

import com.fasterxml.jackson.databind.ObjectMapper
import io.holixon.axon.avro.fixtures.schema.SchemaFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


internal class RecordMetaDataTest {

  private val om: ObjectMapper = ObjectMapper()

  @Test
  fun `extract recordMetaData from schema`() {


    val schema = SchemaFixtures.schemaCreateBankAccount

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
