package io.holixon.axon.avro.fixtures.schema

import io.holixon.axon.avro.fixtures.schema.AvroFqn.Companion.file
import org.apache.avro.JsonProperties
import org.apache.avro.Protocol
import org.apache.avro.Schema
import java.io.File
import java.net.URL
import java.nio.charset.Charset

const val SUFFIX_SCHEMA = "avsc"
const val SUFFIX_PROTOCOL = "avpr"

/**
 * Write content to given file, creates directory path if it not exists.
 */
fun File.writeText(content: String): File {
  if (!this.parentFile.exists()) {
    this.parentFile.mkdirs()
  }
  this.writeText(content, Charset.defaultCharset())
  return this
}

fun namespaceToPath(namespace: String) = namespace.replace(".", File.separator)

fun Schema.fqn() = SchemaFqn(namespace, name)
fun Protocol.fqn() = ProtocolFqn(namespace, name)

val Protocol.path: String get() = AvroFqn.path(namespace, name, SUFFIX_PROTOCOL)
fun Protocol.file(dir: File): File = dir.file(this.path)
fun Protocol.writeToDirectory(dir: File): File = file(dir).writeText(this.toString(true))

val Schema.path: String get() = AvroFqn.path(namespace, name, SUFFIX_SCHEMA)
fun Schema.file(dir: File): File = dir.file(this.path)
fun Schema.writeToDirectory(dir: File): File = file(dir).writeText(this.toString(true))

sealed class AvroFqn<T : JsonProperties>(val suffix: String) {
  companion object {
    fun path(namespace: String, name: String, suffix: String): String = "${namespace.replace(".", File.separator)}${File.separator}${name}.${suffix}"
    fun File.file(path: String): File = File("${this.path}${File.separator}$path")
  }

  abstract val namespace: String
  abstract val name: String
  abstract fun fromResource(prefix: String? = null, classLoader: ClassLoader = AvroFqn::class.java.classLoader): T
  abstract fun fromDirectory(dir: File): T

  val path: String by lazy { path(namespace, name, suffix) }
  fun file(dir: File): File = dir.file(path)
  fun resource(prefix: String? = null, classLoader: ClassLoader = SchemaFixtures::class.java.classLoader): URL {
    val resource = (if (prefix != null) {
      "$prefix${File.separator}$path"
    } else {
      path
    }).removePrefix("/")
    return requireNotNull(classLoader.getResource(resource)) { "resource not found: $path" }
  }
  override fun toString() = "${this::class.simpleName}(namespace='$namespace', name='$name', suffix='$suffix')"
}

data class SchemaFqn(override val namespace: String, override val name: String) : AvroFqn<Schema>(SUFFIX_SCHEMA) {
  override fun fromResource(prefix: String?, classLoader: ClassLoader): Schema = resource(prefix, classLoader).openStream().use {
    Schema.Parser().parse(it)
  }

  override fun fromDirectory(dir: File): Schema = Schema.Parser().parse(file(dir))

  override fun toString(): String = super.toString()
}

data class ProtocolFqn(override val namespace: String, override val name: String) : AvroFqn<Protocol>(SUFFIX_PROTOCOL) {
  override fun fromResource(prefix: String?, classLoader: ClassLoader): Protocol = resource(prefix, classLoader).openStream().use {
    Protocol.parse(it)
  }

  override fun fromDirectory(dir: File): Protocol = Protocol.parse(file(dir))

  override fun toString(): String = super.toString()
}
