package io.holixon.axon.avro.fixtures.schema

import org.apache.avro.Protocol
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SchemaFixturesTest {

  @Test
  fun `can load protocol`() {
    val protocol = SchemaFixtures.Helper.loadProtocol(namespace = "io.holixon.axon.avro.fixtures.schema.query", name = "FindCurrentBalance")

    assertThat(protocol.namespace).isEqualTo("io.holixon.axon.avro.fixtures.schema.query")
    assertThat(protocol.name).isEqualTo("FindCurrentBalance")
  }

  @Test
  fun `text from path`() {
    val text = SchemaFixtures.Helper.readText(
      namespace = "io.holixon.axon.avro.fixtures.schema.query",
      name = "FindCurrentBalance",
      suffix = "avpr")

    assertThat(text).isNotNull

    assertThat(Protocol.parse(text)).isNotNull
  }

  @Test
  fun `build path`() {
    assertThat(SchemaFixtures.Helper.path(
      namespace = "io.holixon.axon.avro.fixtures.schema.query",
      name = "FindCurrentBalance",
      suffix = "avpr"
    )).isEqualTo("io/holixon/axon/avro/fixtures/schema/query/FindCurrentBalance.avpr")
  }
}
