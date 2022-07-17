package io.holixon.axon.avro.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.holixon.axon.avro.types.protocol.ListMessageResponseType
import io.holixon.axon.avro.types.protocol.MessageResponseType
import io.holixon.axon.avro.types.protocol.OptionalMessageResponseType
import io.holixon.axon.avro.types.protocol.SingleMessageResponseType
import io.toolisticon.lib.avro.fqn.SchemaFqn
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture

object AxonAvroGenerator {

  val SchemaFqn.className get() = ClassName(this.namespace, this.name)

  // empty root

  val MessageResponseType.typeName: TypeName
    get() = when (this) {
      is ListMessageResponseType -> List::class.asClassName().parameterizedBy(fqn.className)
      is OptionalMessageResponseType -> Optional::class.asClassName().parameterizedBy(fqn.className)
      is SingleMessageResponseType -> fqn.className
    }

  fun TypeName.wrapInCompletableFuture(): ParameterizedTypeName = CompletableFuture::class.asClassName()
    .parameterizedBy(this)

  fun FileSpec.fileName() = if (packageName.isBlank())
    this.name
  else this.packageName.replace(".", File.separator) + File.separator + this.name + ".kt"
}

fun String.capitalize(): String = replaceFirstChar {
  if (it.isLowerCase()) it.titlecase(Locale.getDefault())
  else it.toString()
}
