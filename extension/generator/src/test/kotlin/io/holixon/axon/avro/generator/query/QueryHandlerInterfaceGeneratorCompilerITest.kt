package io.holixon.axon.avro.generator.query

import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import io.holixon.axon.avro.types.AxonAvroTypes.message
import io.holixon.axon.avro.generator.TestQueryHelper
import io.holixon.axon.avro.generator.TestQueryHelper.protocol
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.io.File


internal class QueryHandlerInterfaceGeneratorCompilerITest {


  @TempDir
  lateinit var tempDir: File

  enum class CreateInterfaceParameter(
    val messageName : String,
    val expected: String
  ) {
    LOAD_ONE("loadOne", """
      public interface LoadOneQueryHandler {
        public fun loadOne(query: generator.test.LoadOne): generator.test.Result
      }
    """.trimIndent()),
    FIND_ONE("findOne", """
      public interface FindOneQueryHandler {
        public fun findOne(query: generator.test.FindOne): java.util.Optional<generator.test.Result>
      }
    """.trimIndent()),
    FIND_ALL("findAll", """
      public interface FindAllQueryHandler {
        public fun findAll(query: generator.test.FindAll): kotlin.collections.List<generator.test.Result>
      }
    """.trimIndent()),
    ;
  }

  @ParameterizedTest
  @EnumSource(CreateInterfaceParameter::class)
  fun `create interface declaration`(parameter: CreateInterfaceParameter) {
    val generator = QueryHandlerInterfaceGenerator(protocol)

    val typeSpec = generator.createInterface(protocol.message(parameter.messageName))

    assertThat(typeSpec.toString().trim()).isEqualTo(parameter.expected)
  }

  @Test
  fun `create all interfaces in one file`() {
    val generatedFile = QueryHandlerInterfaceGenerator(protocol).save(tempDir)

    println(generatedFile.readText())

    assertThat(TestQueryHelper.compile(generatedFile).exitCode).isEqualTo(ExitCode.OK)

    assertThat(generatedFile.readText().trim()).isEqualTo("""
      package generator.test

      import java.util.Optional
      import kotlin.collections.List

      public interface FindAllQueryHandler {
        public fun findAll(query: FindAll): List<Result>
      }

      public interface FindOneQueryHandler {
        public fun findOne(query: FindOne): Optional<Result>
      }

      public interface LoadOneQueryHandler {
        public fun loadOne(query: LoadOne): Result
      }
    """.trimIndent())
  }
}
