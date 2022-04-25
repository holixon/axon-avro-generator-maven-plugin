package io.holixon.axon.avro.generator

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.apache.avro.Protocol
import java.io.File

object TestQueryHelper {

  val protocol: Protocol = requireNotNull(TestQueryHelper::class.java.classLoader.getResourceAsStream("avro/query.avpr"))
    .let { Protocol.parse(it) }

  fun compile(generatedFile: File) : KotlinCompilation.Result = KotlinCompilation().apply {
    sources = listOf(SourceFile.fromPath(generatedFile), typeSources)
    inheritClassPath = true
    verbose = false
  }.compile()


  /**
   * We have to provide the types used in schema file so they are present when we do kotlinpoet compile tests.
   */
  val typeSources = SourceFile.kotlin("TestQueryTypes.kt", """
    package generator.test

    data class Result(val id:String, val name:String)
    class FindAll()
    data class FindOne(val id:String)
    data class LoadOne(val id:String)

  """.trimIndent())

}
