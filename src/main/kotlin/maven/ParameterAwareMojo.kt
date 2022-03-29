package io.holixon.avro.maven.maven

import io.toolisticon.maven.mojo.ComponentAwareMojo

/**
 * Define all your parameter injections here as private attributes.
 * Maven will inject them into this intermediate layer and you can
 * use the to create a configuration instance of your desired type <T>.
 */
abstract class ParameterAwareMojo<T> : ComponentAwareMojo() {

  abstract val configuration: T

}
