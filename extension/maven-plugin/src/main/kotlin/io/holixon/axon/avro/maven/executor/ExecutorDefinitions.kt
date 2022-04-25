package io.holixon.axon.avro.maven.executor

import io.toolisticon.maven.MojoContext
import mu.KLogger
import org.apache.maven.model.Plugin
import org.twdata.maven.mojoexecutor.MojoExecutor.*


internal abstract class AbstractExecutor(
  protected val components: MojoContext
) : Runnable {

  protected val log: KLogger get() = components.logger

  protected val environment: ExecutionEnvironment = components.executionEnvironment
}


internal abstract class AbstractMojoExecutor(
  protected val groupId: String,
  protected val artifactId: String,
  protected val version: String,
  components: MojoContext
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
