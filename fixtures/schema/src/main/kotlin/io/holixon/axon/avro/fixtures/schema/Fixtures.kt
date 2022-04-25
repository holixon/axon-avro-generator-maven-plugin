package io.holixon.axon.avro.fixtures.schema

import io.holixon.axon.avro.fixtures.schema.Fixtures.Helper.loadProtocol
import io.holixon.axon.avro.fixtures.schema.Fixtures.Helper.loadSchema
import org.apache.avro.Protocol
import org.apache.avro.Schema
import java.io.File

object Fixtures {

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
    fun loadSchema(namespace: String, name: String): Schema = Schema.Parser().parse(readText(namespace, name, "avsc"))

    fun loadProtocol(namespace: String, name: String): Protocol = Protocol.parse(readText(namespace, name, "avpr"))

    fun readText(namespace: String, name: String, suffix: String): String {
      val path = path(namespace, name, suffix)
      return requireNotNull(Fixtures::class.java.classLoader.getResource(path)) { "resource not found: $path" }.readText()
    }

    fun path(namespace: String, name: String, suffix: String): String = "${namespace.replace(".", File.separator)}${File.separator}${name}.${suffix}"
  }
}
