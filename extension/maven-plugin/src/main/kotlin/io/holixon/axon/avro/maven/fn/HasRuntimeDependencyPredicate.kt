package io.holixon.axon.avro.maven.fn

import io.toolisticon.maven.model.ArtifactId
import io.toolisticon.maven.model.GroupId
import io.toolisticon.maven.mojo.MavenExt.hasRuntimeDependency
import org.apache.maven.project.MavenProject
import java.util.function.BiPredicate

open class HasRuntimeDependencyPredicate(
  val project: MavenProject?
) : BiPredicate<GroupId, ArtifactId> {

  override fun test(groupId: GroupId, artifactId: ArtifactId): Boolean = project?.hasRuntimeDependency(groupId, artifactId)
    ?: false
}
