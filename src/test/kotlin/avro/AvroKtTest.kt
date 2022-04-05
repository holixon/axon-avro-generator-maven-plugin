package io.holixon.avro.maven.avro

import io.holixon.avro.maven.TestFixtures
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.PrintWriter

internal class AvroKtTest {

  private val parser = Schema.Parser()

  @TempDir
  private lateinit var tmpDir: File

  @Test
  fun `can parse schema file`() {
    val fqnFile = TestFixtures.createFqnPath(tmpDir, "io.holixon.schema.bank.event")
    val avscFile = File("$fqnFile/BalanceChangedEvent.avsc")

    PrintWriter(avscFile, Charsets.UTF_8).use {
      it.write(TestFixtures.balanceChangedEventAvsc)
    }

    val schema = canParseSchema(avscFile, parser)

    assertThat(schema.namespace).isEqualTo("io.holixon.schema.bank.event")
    assertThat(schema.name).isEqualTo("BalanceChangedEvent")
  }

  @Test
  fun `schema fqn matches path - everything ok`() {
    val fqnFile = TestFixtures.createFqnPath(tmpDir, "io.holixon.schema.bank.event")
    val avscFile = File("$fqnFile/BalanceChangedEvent.avsc")

    PrintWriter(avscFile, Charsets.UTF_8).use {
      it.write(TestFixtures.balanceChangedEventAvsc)
    }

    val schema = verifyPathAndSchemaFqnMatches(tmpDir, avscFile, parser)

    assertThat(schema.namespace).isEqualTo("io.holixon.schema.bank.event")
    assertThat(schema.name).isEqualTo("BalanceChangedEvent")
  }

  @Test
  fun `cannot parse schema file due to missing comma`() {
    val fqnFile = TestFixtures.createFqnPath(tmpDir, "io.holixon.schema.bank.event")
    val avscFile = File("$fqnFile/BalanceChangedEvent.avsc")

    PrintWriter(avscFile, Charsets.UTF_8).use {
      it.write(TestFixtures.balanceChangedEventNotParsableDueToExtraCommaAvsc)
    }

    assertThatThrownBy { canParseSchema(avscFile, parser) }.isInstanceOf(CannotParseAvroSchemaException::class.java)
  }


  @Test
  fun `schema namespace&name does not match path`() {
    val fqnFile = TestFixtures.createFqnPath(tmpDir, "io.holixon.schema.bank")
    val avscFile = File("$fqnFile/BalanceChangedEvent.avsc")

    PrintWriter(avscFile, Charsets.UTF_8).use {
      it.write(TestFixtures.balanceChangedEventAvsc)
    }

    assertThatThrownBy { verifyPathAndSchemaFqnMatches(tmpDir, avscFile, parser) }.isInstanceOf(AvroSchemaFqnMismatch::class.java)
  }

  @Test
  fun `avro SCHEMA field to RecordMetadata`() {
    val field = """new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"BankAccountCreatedEvent\",\"namespace\":\"io.holixon.schema.bank.event\",\"doc\":\"A bank account has been created\",\"fields\":[{\"name\":\"accountId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"initialBalance\",\"type\":\"int\"},{\"name\":\"maximalBalance\",\"type\":\"int\"}],\"meta\":{\"type\":\"event\",\"revision\":\"1\"}}")"""

    val x: Schema = schemaFieldToRecordMetadata(field)!!

    println(RecordMetaData(x))
  }

}
