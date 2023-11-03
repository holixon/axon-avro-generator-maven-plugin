package io.holixon.axon.avro.fixtures.schema

import io.holixon.axon.avro.fixtures.schema.AvroFqn.Companion.file
import org.apache.avro.JsonProperties
import org.apache.avro.Protocol
import org.apache.avro.Schema
import java.io.File
import java.net.URL
import java.nio.charset.Charset

/**
 * Avro Schema files end with `.avsc`.
 */
const val SUFFIX_SCHEMA = "avsc"

/**
 * Avro protocol files end with `.avpr`.
 */
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

/**
 * Converts a namespace (FQN/package) to file path.
 *
 * So `io.foo.bar` becomes `io/foo/bar`.
 */
fun namespaceToPath(namespace: String) = namespace.replace(".", File.separator)

/**
 * Shortcut to create a [SchemaFqn] from [Schema].
 */
fun Schema.fqn() = SchemaFqn(namespace, name)

/**
 * Shortcut to create [ProtocolFqn] from [Protocol].
 */
fun Protocol.fqn() = ProtocolFqn(namespace, name)

/**
 * File-Path for [Protocol] based on namespace and name.
 */
val Protocol.path: String get() = AvroFqn.path(namespace, name, SUFFIX_PROTOCOL)

/**
 * A [File] with [Protocol#path] based on given directory. Use to read or write protocol from file system.
 */
fun Protocol.file(dir: File): File = dir.file(this.path)

/**
 * Write protocol json content to directory, using [Protocol#path].
 */
fun Protocol.writeToDirectory(dir: File): File = file(dir).writeText(this.toString(true))

/**
 * File-Path for [Schema] based on namespace and name.
 */
val Schema.path: String get() = AvroFqn.path(namespace, name, SUFFIX_SCHEMA)

/**
 * A [File] with [Protocol#path] based on given directory. Use to read or write protocol from file system.
 */
fun Schema.file(dir: File): File = dir.file(this.path)

/**
 * Write schema json content to directory, using [Schema#path].
 */
fun Schema.writeToDirectory(dir: File): File = file(dir).writeText(this.toString(true))

/**
 * Common setup for [ProtocolFqn] and [AvroFqn].
 *
 * This supports reading content from resources or files and write content to files.
 */
sealed class AvroFqn<T : JsonProperties>(val suffix: String) {
  companion object {
    /**
     * Path based on namespace, name and suffix.
     */
    fun path(namespace: String, name: String, suffix: String): String = "${namespace.replace(".", File.separator)}${File.separator}${name}.${suffix}"
    fun File.file(path: String): File = File("${this.path}${File.separator}$path")
  }

  /**
   * Namespace as in schema or protocol.
   */
  abstract val namespace: String

  /**
   * Name as in schema or protocol.
   */
  abstract val name: String

  /**
   * Load from resource.
   *
   * @param prefix optional path inside the classpath if not in root
   * @param optional classloader if specific one is required, otherwise classloader of AvroFqn is used
   * @return parsed avro schema or protocol instance
   */
  abstract fun fromResource(prefix: String? = null, classLoader: ClassLoader = AvroFqn::class.java.classLoader): T

  /**
   * Load from file.
   *
   * @param the file to read from
   * @return parsed avro schema or protocol instance
   */
  abstract fun fromDirectory(dir: File): T

  /**
   * The full path of the fqn using namespace hierarchy and suffix.
   */
  val path: String by lazy { path(namespace, name, suffix) }

  /**
   * [File] using [#path] and given root dir.
   * @param dir the root directory from which the path is taken
   * @return file representing the namespace and name and suffix path
   */
  fun file(dir: File): File = dir.file(path)

  /**
   * URL of resource using path based on namespace and name.
   * @param prefix optional prefix if resource is not under root in classpath
   * @param classLoader optional classloader if not given, the classloader of AvroFqn is used
   * @return URL pointing to resource.
   */
  fun resource(prefix: String? = null, classLoader: ClassLoader = AvroFqn::class.java.classLoader): URL {
    val resource = (if (prefix != null) {
      "$prefix${File.separator}$path"
    } else {
      path
    }).removePrefix("/")
    return requireNotNull(classLoader.getResource(resource)) { "resource not found: $path" }
  }

  override fun toString() = "${this::class.simpleName}(namespace='$namespace', name='$name', suffix='$suffix')"
}

/**
 * Implementation of [AvroFqn] for [Schema].
 */
data class SchemaFqn(override val namespace: String, override val name: String) : AvroFqn<Schema>(SUFFIX_SCHEMA) {
  override fun fromResource(prefix: String?, classLoader: ClassLoader): Schema = resource(prefix, classLoader).openStream().use {
    Schema.Parser().parse(it)
  }

  override fun fromDirectory(dir: File): Schema = Schema.Parser().parse(file(dir))

  override fun toString(): String = super.toString()
}

/**
 * Implementation of [AvroFqn] for [Protocol].
 */
data class ProtocolFqn(override val namespace: String, override val name: String) : AvroFqn<Protocol>(SUFFIX_PROTOCOL) {
  override fun fromResource(prefix: String?, classLoader: ClassLoader): Protocol = resource(prefix, classLoader).openStream().use {
    Protocol.parse(it)
  }

  override fun fromDirectory(dir: File): Protocol = Protocol.parse(file(dir))

  override fun toString(): String = super.toString()
}
