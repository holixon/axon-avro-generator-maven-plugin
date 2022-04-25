package io.holixon.axon.avro.generator

import io.holixon.axon.avro.types.AxonAvroTypes
import io.holixon.axon.avro.types.protocol.ListMessageResponseType
import io.holixon.axon.avro.types.protocol.OptionalMessageResponseType
import io.holixon.axon.avro.types.protocol.SingleMessageResponseType
import io.holixon.axon.avro.generator.AxonAvroGenerator.typeName
import io.holixon.axon.avro.generator.AxonAvroGenerator.wrapInCompletableFuture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class AxonAvroGeneratorTest {

  @ParameterizedTest
  @CsvSource(
    value = [
      "java.lang,String,S,false,java.lang.String",
      "java.lang,String,O,false,java.util.Optional<java.lang.String>",
      "java.lang,String,L,false,kotlin.collections.List<java.lang.String>",
      "java.lang,String,S,true,java.util.concurrent.CompletableFuture<java.lang.String>",
      "java.lang,String,O,true,java.util.concurrent.CompletableFuture<java.util.Optional<java.lang.String>>",
      "java.lang,String,L,true,java.util.concurrent.CompletableFuture<kotlin.collections.List<java.lang.String>>",
    ]
  )
  fun `get type names from message response`(namespace: String, name: String, type: String, wrapInFuture: Boolean, expected: String) {
    val fqn = AxonAvroTypes.SchemaFqn(namespace, name)

    val responseType = when (type) {
      "L" -> ListMessageResponseType(fqn)
      "O" -> OptionalMessageResponseType(fqn)
      else -> SingleMessageResponseType(fqn)
    }

    val typeName = if (wrapInFuture) responseType.typeName.wrapInCompletableFuture() else responseType.typeName

    assertThat(typeName.toString().trim()).isEqualTo(expected)
  }
}
