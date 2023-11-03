package io.holixon.axon.avro.fixtures.schema

import io.holixon.axon.avro.fixtures.schema.SchemaFixtures.Helper.loadProtocol
import io.holixon.axon.avro.fixtures.schema.SchemaFixtures.Helper.loadSchema
import org.apache.avro.JsonProperties
import org.apache.avro.Protocol
import org.apache.avro.Schema
import java.io.File
import java.nio.charset.Charset

object SchemaFixtures {

  val protocolFindCurrentBalance: Protocol = loadProtocol(
    namespace = "io.holixon.axon.avro.fixtures.schema.query",
    name = "FindCurrentBalance"
  )

  val schemaCreateBankAccount = loadSchema(
    namespace = "io.holixon.axon.avro.fixtures.schema.command",
    name = "CreateBankAccount"
  )

  val schemaBankAccountCreated = loadSchema(
    namespace = "io.holixon.axon.avro.fixtures.schema.event",
    name = "BankAccountCreated"
  )

  object Helper {
    const val SUFFIX_SCHEMA = "avsc"
    const val SUFFIX_PROTOCOL = "avpr"

    @Deprecated("use SchemaFqn.fromResource")
    fun loadSchema(namespace: String, name: String): Schema = Schema.Parser().parse(readText(namespace, name, SUFFIX_SCHEMA))

    @Deprecated("use ProtocolFqn.fromResource")
    fun loadProtocol(namespace: String, name: String): Protocol = Protocol.parse(readText(namespace, name, SUFFIX_PROTOCOL))

    @Deprecated("use SchemaFqn.fromResource/ProtocolFqn.fromResource")
    fun readText(namespace: String, name: String, suffix: String): String {
      val path = path(namespace, name, suffix)
      return requireNotNull(SchemaFixtures::class.java.classLoader.getResource(path)) { "resource not found: $path" }.readText()
    }

    fun path(namespace: String, name: String, suffix: String): String = "${namespace.replace(".", File.separator)}${File.separator}${name}.${suffix}"


    fun Schema.filePath() = path(namespace, name, SUFFIX_SCHEMA)

    fun Schema.writeTo(dir: File) {
      val target = File("${dir.path}${File.separator}${filePath()}")
      target.mkdirs()
      target.writeText(this.toString(true), Charset.defaultCharset())
    }
  }
}
