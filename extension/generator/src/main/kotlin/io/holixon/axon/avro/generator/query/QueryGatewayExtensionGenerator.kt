package io.holixon.axon.avro.generator.query

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.MemberName.Companion.member
import io.holixon.axon.avro.types.protocol.*
import io.holixon.axon.avro.generator.AxonAvroGenerator.className
import io.holixon.axon.avro.generator.AxonAvroGenerator.typeName
import io.holixon.axon.avro.generator.AxonAvroGenerator.wrapInCompletableFuture
import org.apache.avro.Protocol
import org.apache.avro.Protocol.Message
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway

class QueryGatewayExtensionGenerator(protocol: Protocol) : AbstractProtocolGenerator(protocol) {
  companion object {
    val responseTypesClass = ResponseTypes::class.asClassName()

    const val RESPONSE_TYPE_FACTORY_SINGLE = "instanceOf"
    const val RESPONSE_TYPE_FACTORY_LIST = "multipleInstancesOf"
    const val RESPONSE_TYPE_FACTORY_OPTIONAL = "optionalInstanceOf"

    val MessageResponseType.memberName: MemberName
      get() = when (this) {
        is ListMessageResponseType -> responseTypesClass.member(RESPONSE_TYPE_FACTORY_LIST)
        is OptionalMessageResponseType -> responseTypesClass.member(RESPONSE_TYPE_FACTORY_OPTIONAL)
        is SingleMessageResponseType -> responseTypesClass.member(RESPONSE_TYPE_FACTORY_SINGLE)
      }
  }

  fun createQueryFn(message: Message): FunSpec {

    val responseType = message.messageResponseType
    val queryParameter = message.queryParameter
    val responseTypeMember = message.messageResponseType.memberName

    return FunSpec.builder("query")
      .receiver(QueryGateway::class)
      .addParameter(queryParameter)
      .returns(message.messageResponseType.typeName.wrapInCompletableFuture())
      .addStatement(
        "return this.query(${queryParameter.name}, %M(%T::class.java))", responseTypeMember, responseType.fqn.className
      )
      .build()
  }

  override val className: ClassName = ClassName(protocol.namespace, protocol.name + "QueryGatewayExt")

  override fun invoke(): FileSpec = className.fileSpec().apply {
    val objectSpec = TypeSpec.objectBuilder(className)

    protocol.messages.values.map { createQueryFn(it) }.forEach {
      objectSpec.addFunction(it)
    }
  }.build()

}
