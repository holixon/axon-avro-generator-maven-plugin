package io.holixon.axon.avro.generator.protocol

import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.OK
import io.holixon.axon.avro.types.AxonAvroTypes.message
import io.holixon.axon.avro.generator.TestQueryHelper
import io.holixon.axon.avro.generator.TestQueryHelper.protocol
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.io.File

internal class QueryGatewayExtensionGeneratorTest {

  enum class CreateQueryGatewayExtensionParameter(
    val messageName: String,
    val expected: String
  ) {
    LOAD_ONE(
      "loadOne",
      "public fun org.axonframework.queryhandling.QueryGateway.query(query: generator.test.LoadOne): " +
        "java.util.concurrent.CompletableFuture<generator.test.Result> " +
        "= this.query(query, org.axonframework.messaging.responsetypes.ResponseTypes.instanceOf(generator.test.Result::class.java))"
    ),
    FIND_ONE(
      "findOne",
      "public fun org.axonframework.queryhandling.QueryGateway.query(query: generator.test.FindOne): " +
        "java.util.concurrent.CompletableFuture<java.util.Optional<generator.test.Result>> " +
        "= this.query(query, org.axonframework.messaging.responsetypes.ResponseTypes.optionalInstanceOf(generator.test.Result::class.java))"
    ),
    FIND_ALL(
      "findAll",
      "public fun org.axonframework.queryhandling.QueryGateway.query(query: generator.test.FindAll): " +
        "java.util.concurrent.CompletableFuture<kotlin.collections.List<generator.test.Result>> " +
        "= this.query(query, org.axonframework.messaging.responsetypes.ResponseTypes.multipleInstancesOf(generator.test.Result::class.java))"
    ),
    ;
  }

  @ParameterizedTest
  @EnumSource(CreateQueryGatewayExtensionParameter::class)
  fun `create queryGatewayExtension`(parameter: CreateQueryGatewayExtensionParameter) {
    val generator = QueryGatewayExtensionGenerator(protocol)

    val message = protocol.message(parameter.messageName)
    val spec = generator.createQueryFn(message)

    assertThat(spec.toString().trim()).isEqualTo(parameter.expected)
  }

  @TempDir
  lateinit var tmp: File

  @Test
  fun `create and compile file`() {
    val generatedFile = QueryGatewayExtensionGenerator(protocol).save(tmp)

    assertThat(generatedFile.readText().trim()).isEqualTo("""
      package generator.test

      import java.util.Optional
      import java.util.concurrent.CompletableFuture
      import kotlin.collections.List
      import org.axonframework.messaging.responsetypes.ResponseTypes.instanceOf
      import org.axonframework.messaging.responsetypes.ResponseTypes.multipleInstancesOf
      import org.axonframework.messaging.responsetypes.ResponseTypes.optionalInstanceOf
      import org.axonframework.queryhandling.QueryGateway

      public object TestQueryQueryGatewayExt {
        public fun QueryGateway.query(query: FindAll): CompletableFuture<List<Result>> = this.query(query,
            multipleInstancesOf(Result::class.java))

        public fun QueryGateway.query(query: FindOne): CompletableFuture<Optional<Result>> =
            this.query(query, optionalInstanceOf(Result::class.java))

        public fun QueryGateway.query(query: LoadOne): CompletableFuture<Result> = this.query(query,
            instanceOf(Result::class.java))
      }
    """.trimIndent())

    assertThat(TestQueryHelper.compile(generatedFile).exitCode).isEqualTo(OK)
  }
}

