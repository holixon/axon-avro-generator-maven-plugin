package io.holixon.axon.avro.generator.query

import com.squareup.kotlinpoet.*
import io.holixon.axon.avro.types.protocol.messageResponseType
import io.holixon.axon.avro.generator.AxonAvroGenerator.typeName
import org.apache.avro.Protocol
import org.apache.avro.Protocol.Message


class QueryHandlerInterfaceGenerator(
  protocol: Protocol
) : AbstractProtocolGenerator(protocol) {
  companion object {
    const val INTERFACE_SUFFIX = "QueryHandler"

    private val Message.interfaceName: String get() = name.capitalize() + INTERFACE_SUFFIX
  }

  fun createInterface(message: Message): TypeSpec {
    val responseType = message.messageResponseType

    val funSpec = FunSpec.builder(message.name)
      .addModifiers(KModifier.ABSTRACT)
      .addParameter(message.queryParameter)

    funSpec.returns(responseType.typeName)

    return TypeSpec.interfaceBuilder(ClassName(protocol.namespace, message.interfaceName))
      .addFunction(
        funSpec.build()
      ).build()
  }

  override val className: ClassName = ClassName(protocol.namespace, "${protocol.name}${INTERFACE_SUFFIX}s")

  override fun invoke(): FileSpec = className.fileSpec().apply {
    protocol.messages.values.map { createInterface(it) }.forEach {
      this.addType(it)
    }
  }.build()
}
