package io.holixon.avro.maven.avro

import io.toolisticon.maven.MojoCommand
import io.toolisticon.maven.model.Configuration
import io.toolisticon.maven.model.Goal
import io.toolisticon.maven.mojo.MojoExecutorDsl
import io.toolisticon.maven.mojo.MojoExecutorDsl.configuration
import io.toolisticon.maven.plugin.PluginWrapper
import org.apache.maven.model.Plugin

object AvroMavenPlugin : PluginWrapper {
  override val plugin: Plugin = MojoExecutorDsl.plugin(groupId = "org.apache.avro", artifactId = "avro-maven-plugin", version = "1.11.0")

  class AvroSchemaCommand() : MojoCommand {
    companion object {
      const val GOAL = "schema"
    }
    override val configuration: Configuration = configuration {

    }

    override val goal: Goal = GOAL
    override val plugin : Plugin = AvroMavenPlugin.plugin
    override fun toString() = MojoCommand.toString(this)

  }
}
