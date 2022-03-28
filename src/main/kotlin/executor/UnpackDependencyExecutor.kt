package io.holixon.avro.maven.executor

import io.holixon.avro.maven.maven.MojoComponents
import org.twdata.maven.mojoexecutor.MojoExecutor.*
import java.io.File

internal class UnpackDependencyExecutor(
  components: MojoComponents
) : AbstractMojoExecutor(
  groupId = "org.apache.maven.plugins",
  artifactId = "maven-dependency-plugin",
  version = "3.3.0",
  components = components
) {
  companion object {
    const val GOAL = "unpack"
  }

  private lateinit var _outputDirectory: File
  private lateinit var schemaArtifacts: ArtifactItems
  private lateinit var includeSchemas: Set<String>


  fun outputDirectory(outputDirectory: File) = apply {
    this._outputDirectory = outputDirectory
  }

  fun schemaArtifacts(schemaArtifacts: Set<String>) = apply {
    this.schemaArtifacts = schemaArtifacts.fold(ArtifactItems()) { items, gav ->
      val (groupId, artifactId, version) = gav.trim().split(":")
      items.add(ArtifactItem(groupId, artifactId, version))
      items
    }
  }

  fun includeSchemas(includeSchemas: Set<String>) = apply {
    this.includeSchemas = includeSchemas.map { it.trim() }
      .map { it.removeSuffix(".avsc") }
      .map { it.replace(".", "/") }
      .map { it.plus(".avsc") }
      .toSortedSet()
  }


  private fun elementArtifactItems(): Element = schemaArtifacts.element()

  private fun elementIncludeSchemas(): Element = element("includes", includeSchemas.joinToString(","))

  override fun run() = executeMojo(
    GOAL,
    element(name("outputDirectory"), _outputDirectory.path),
    elementArtifactItems(),
    elementIncludeSchemas(),
    element("excludes", "META-INF/**")
  )

  data class ArtifactItems(val list: MutableList<ArtifactItem> = mutableListOf()) : MutableList<ArtifactItem> by list, ElementSupplier {
    override fun element(): Element = element("artifactItems", *list.map { it.element() }.toTypedArray())
  }

  data class ArtifactItem(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val overwrite: Boolean = false
  ) : ElementSupplier {

    override fun element(): Element = element(
      "artifactItem",
      element("groupId", groupId),
      element("artifactId", artifactId),
      element("version", version),
      element("overWrite", "$overwrite")
    )
  }
}
