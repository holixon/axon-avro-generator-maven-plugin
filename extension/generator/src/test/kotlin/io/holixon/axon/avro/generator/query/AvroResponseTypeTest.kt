package io.holixon.axon.avro.generator.query

import org.apache.avro.Protocol
import org.junit.jupiter.api.Test

internal class AvroResponseTypeTest {

  @Test
  fun `determine response type from message`() {
    val p = Protocol.parse("""
      {"namespace":"io.test", "protocol": "HelloWorld",
       "messages": {
         "hello": {
           "request": [{"name": "greeting", "type": "string" }],
           "response": "string"
       }}}
    """.trimIndent())

    println(p)
  }
}
