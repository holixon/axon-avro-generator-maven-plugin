package io.holixon.avro.maven.executor

import org.apache.maven.model.Plugin
import org.twdata.maven.mojoexecutor.MojoExecutor
import org.twdata.maven.mojoexecutor.MojoExecutor.*

internal abstract class AbstractMojoExecutor(
  protected val groupId: String,
  protected val artifactId: String,
  protected val version: String,
  protected val environment : ExecutionEnvironment
) : Runnable {

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

  fun element() : Element

}
