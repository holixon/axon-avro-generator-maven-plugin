package io.holixon.axon.avro.generator.query

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterSpec
import io.holixon.axon.avro.types.AxonAvroTypes.fqn
import io.holixon.axon.avro.generator.AxonAvroGenerator.className
import io.holixon.axon.avro.generator.AxonAvroGenerator.fileName
import org.apache.avro.Protocol
import java.io.File

abstract class AbstractProtocolGenerator(
  protected val protocol: Protocol
) : () -> FileSpec {

  abstract val className: ClassName

  val Protocol.Message.queryParameter: ParameterSpec
    get() {
      // queryGatewayProtocol can only have one parameter, single fails in other cases!
      val (name, className) = this.request.fields.map { it.name() to it.schema().fqn.className }.single()
      return ParameterSpec.builder(name, className).build()
    }

  fun ClassName.fileSpec() = FileSpec.builder(packageName, this.simpleName)

  fun save(dir:File) : File {
    val spec = invoke()

    spec.writeTo(dir)

    return File(dir.path + File.separator + spec.fileName())
  }
}
