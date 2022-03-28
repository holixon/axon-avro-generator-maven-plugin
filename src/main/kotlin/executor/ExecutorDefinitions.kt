package io.holixon.avro.maven.executor

import io.holixon.avro.maven.maven.MojoComponents
import mu.KLogger
import org.apache.maven.model.Plugin
import org.twdata.maven.mojoexecutor.MojoExecutor.*


internal abstract class AbstractExecutor(
  protected val components: MojoComponents
) : Runnable {

  protected val log: KLogger get() = components.logger

  protected val environment: ExecutionEnvironment = components.environment
}


internal abstract class AbstractMojoExecutor(
  protected val groupId: String,
  protected val artifactId: String,
  protected val version: String,
  components: MojoComponents
) : AbstractExecutor(components) {

  protected val plugin: Plugin = plugin(
    groupId(groupId),
    artifactId(artifactId),
    version(version)
  )

  protected fun executeMojo(goal: String, vararg configuration: Element) = executeMojo(
    plugin,
    goal(goal),
    configuration(*configuration),
    environment
  )
}

internal interface ElementSupplier {

  fun element(): Element

}
