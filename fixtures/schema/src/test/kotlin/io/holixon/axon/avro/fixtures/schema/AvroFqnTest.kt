package io.holixon.axon.avro.fixtures.schema

import org.apache.avro.Protocol
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class AvroFqnTest {

  @TempDir
  private lateinit var tmpDir: File

  @Test
  fun `path is derived from namespace and name`() {
    assertThat(SchemaFqn("foo.bar", "Hello").path).isEqualTo("foo/bar/Hello.avsc")
    assertThat(ProtocolFqn("foo.bar", "Hello").path).isEqualTo("foo/bar/Hello.avpr")
  }

  @Test
  fun `can load protocol from resources`() {
    val fqn = ProtocolFqn(namespace = "io.holixon.axon.avro.fixtures.schema.query", name = "FindCurrentBalance")

    val protocol: Protocol = fqn.fromResource()
    assertThat((protocol.namespace)).isEqualTo(fqn.namespace)
    assertThat((protocol.name)).isEqualTo(fqn.name)
  }

  @Test
  fun `can write protocol to file (and read again)`() {
    val fqn = ProtocolFqn(namespace = "io.holixon.axon.avro.fixtures.schema.query", name = "FindCurrentBalance")

    // copy resource to tmp file
    val protocol = fqn.fromResource()
    val file = protocol.writeToDirectory(tmpDir)

    // read from tmp file
    val readFromFile = fqn.fromDirectory(tmpDir)

    assertThat(readFromFile).isEqualTo(protocol)
  }

  @Test
  fun `can load schema from resources`() {
    val fqn = SchemaFqn(namespace = "io.holixon.axon.avro.fixtures.schema.event", name = "BankAccountCreated")

    val schema: Schema = fqn.fromResource()
    assertThat(schema).isNotNull
    assertThat((schema.namespace)).isEqualTo(fqn.namespace)
    assertThat((schema.name)).isEqualTo(fqn.name)
  }

  @Test
  fun `can write schema to file (and read again)`() {
    val fqn = SchemaFqn(namespace = "io.holixon.axon.avro.fixtures.schema.event", name = "BankAccountCreated")

    // copy resource to tmp file
    val schema = fqn.fromResource()
    val file = schema.writeToDirectory(tmpDir)

    // read from tmp file
    val readFromFile = fqn.fromDirectory(tmpDir)

    assertThat(readFromFile).isEqualTo(schema)
  }
}
