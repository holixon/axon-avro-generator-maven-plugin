package io.holixon.axon.avro.types.protocol

import io.toolisticon.lib.avro.fqn.SchemaFqn
import org.apache.avro.Protocol
import org.apache.avro.Protocol.Message
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.reflect.KClass

internal class MessageResponseTypeTest {

  @ParameterizedTest
  @EnumSource(DetermineMessageResponseTypeParameter::class)
  fun `determine message response type`(parameter: DetermineMessageResponseTypeParameter) {
    val responseType = parameter.message.messageResponseType

    assertThat(responseType).isInstanceOf(parameter.expectedType.java)
    assertThat(responseType.fqn).isEqualTo(parameter.resultFqn)
  }

  enum class DetermineMessageResponseTypeParameter(
    responseJson: String,
    val expectedType: KClass<out MessageResponseType>
  ) {
    SINGLE(
      """
      "Result"
      """.trimIndent(), SingleMessageResponseType::class
    ),
    OPTIONAL(
      """
      ["null","Result"]
    """.trimIndent(), OptionalMessageResponseType::class
    ),
    LIST(
      """
      {
        "type":"array",
        "items":"Result",
        "default":[]
      }
      """.trimIndent(), ListMessageResponseType::class
    ),
    ;

    val namespace = this::class.java.packageName
    val protocolName: String = name.lowercase().replaceFirstChar { it.uppercase() }
    val messageName = name.lowercase()
    val resultFqn = SchemaFqn(namespace, "Result")


    val protocol = Protocol.parse(
      """
      {"namespace":"$namespace", "protocol": "$protocolName",
       "types": [
         {"name": "Query", "type":"record", "fields": [{"name": "id", "type":"int"}]},
         {"name": "Result", "type":"record", "fields": [{"name": "value", "type":"int"}]}
       ],
       "messages": {
         "$messageName": {
           "request": [{"name": "query", "type": "Query" }],
           "response": $responseJson
       }}}
    """.trimIndent()
    )

    val message: Message = requireNotNull(protocol.messages[messageName])
  }
}
