package io.holixon.axon.avro.maven.avro

import io.holixon.axon.avro.maven.TestFixtures
import org.junit.jupiter.api.Test

class GenerateAxonQueryFromProtocolTest {

  val protocol = TestFixtures.loadProtocol("io.holixon.schema.bank.query", "BankAccountQueryProtocol")

  @Test
  fun dummy() {
    println(protocol)
  }
}
