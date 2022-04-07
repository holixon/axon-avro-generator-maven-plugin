package io.holixon.avro.maven.avro

import io.holixon.avro.maven.TestFixtures
import io.holixon.avro.maven.avro.meta.RecordMetaData
import io.holixon.avro.maven.avro.meta.RecordMetaDataType
import io.toolisticon.maven.fn.FileExt.writeString
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class AvroKtTest {

  private val parser = Schema.Parser()

  @TempDir
  private lateinit var tmpDir: File

  @Test
  fun `can parse schema file`() {
    val avscFile: File = tmpDir.writeString("io.holixon.schema.bank.event", "BalanceChangedEvent.avsc", TestFixtures.balanceChangedEventAvsc)

    val schema = canParseSchema(avscFile, parser)

    assertThat(schema.namespace).isEqualTo("io.holixon.schema.bank.event")
    assertThat(schema.name).isEqualTo("BalanceChangedEvent")
  }

  @Test
  fun `schema fqn matches path - everything ok`() {
    val avscFile: File = tmpDir.writeString("io.holixon.schema.bank.event", "BalanceChangedEvent.avsc", TestFixtures.balanceChangedEventAvsc)

    val schema = verifyPathAndSchemaFqnMatches(tmpDir, avscFile, parser)

    assertThat(schema.namespace).isEqualTo("io.holixon.schema.bank.event")
    assertThat(schema.name).isEqualTo("BalanceChangedEvent")
  }

  @Test
  fun `cannot parse schema file due to extra comma`() {
    val avscFile: File =
      tmpDir.writeString("io.holixon.schema.bank.event", "BalanceChangedEvent.avsc", TestFixtures.balanceChangedEventNotParsableDueToExtraCommaAvsc)

    assertThatThrownBy { canParseSchema(avscFile, parser) }.isInstanceOf(CannotParseAvroSchemaException::class.java)
  }


  @Test
  fun `schema namespace&name does not match path`() {
    val avscFile: File = tmpDir.writeString("io.holixon.schema.bank", "BalanceChangedEvent.avsc", TestFixtures.balanceChangedEventAvsc)


    assertThatThrownBy { verifyPathAndSchemaFqnMatches(tmpDir, avscFile, parser) }.isInstanceOf(AvroSchemaFqnMismatch::class.java)
  }

  @Test
  fun `avro SCHEMA field to RecordMetadata`() {
    val field =
      """new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"BankAccountCreatedEvent\",\"namespace\":\"io.holixon.schema.bank.event\",\"doc\":\"A bank account has been created\",\"fields\":[{\"name\":\"accountId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"initialBalance\",\"type\":\"int\"},{\"name\":\"maximalBalance\",\"type\":\"int\"}],\"meta\":{\"type\":\"event\",\"revision\":\"1\"}}")"""

    val recordMetaData = RecordMetaData.parse(schemaFieldToRecordMetadata(field))

    assertThat(recordMetaData).isEqualTo(
      RecordMetaData(
        namespace = "io.holixon.schema.bank.event",
        name = "BankAccountCreatedEvent",
        revision = "1",
        type = RecordMetaDataType.event
      )
    )
  }

}
