package io.holixon.avro.maven.executor

import org.twdata.maven.mojoexecutor.MojoExecutor.*

internal class UnpackDependencyExecutor(
  environment: ExecutionEnvironment
) : AbstractMojoExecutor(
  groupId = "org.apache.maven.plugins",
  artifactId = "maven-dependency-plugin",
  version = "3.3.0",
  environment = environment
) {
  companion object {
    const val GOAL = "unpack"
  }

  private val artifactItems = ArtifactItems()
  private val includeSchemas = mutableListOf<String>()
  private lateinit var outputDirectory: String

  fun addArtifactItem(gav: String) = apply {
    val (groupId, artifactId, version) = gav.split(":")

    artifactItems.add(ArtifactItem(groupId, artifactId, version))
  }

  fun addArtifactItems(gavs: List<String>) = apply {
    gavs.forEach { addArtifactItem(it) }
  }

  fun includeSchema(fqn: String) = apply {
    val path = fqn
      .trim()
      .removeSuffix(".avsc")
      .replace(".", "/")
      .plus(".avsc")

    includeSchemas.add(path)
  }

  fun includeSchemas(fqns: List<String>) = apply {
    fqns.forEach { includeSchema(it) }
  }

  fun outputDirectory(outputDirectory: String) = apply {
    this.outputDirectory = outputDirectory
  }

  override fun run() = executeMojo(
    GOAL,
    element(name("outputDirectory"), outputDirectory),
    artifactItems.element(),
    element("excludes", "META-INF/**"),
    element("includes", includeSchemas.joinToString(","))
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

//  <includes>com/fiege/oms/customermanagement/event/CustomerCreatedEvent.avsc,com/fiege/oms/global/query/LookupCustomerQuery.avsc,com/fiege/oms/global/query/LookupCustomerQueryResult.avsc</includes>
//  <excludes>META-INF/**</excludes>
//</configuration>
