package io.holixon.axon.avro.maven.spoon

import io.holixon.axon.avro.maven.spoon.ext.SpoonExt.typeReference
import io.holixon.axon.avro.types.meta.RecordMetaDataType
import io.toolisticon.maven.model.ArtifactId
import io.toolisticon.maven.model.GroupId
import mu.KLogger
import org.apache.avro.specific.SpecificRecord
import org.apache.avro.specific.SpecificRecordBase
import spoon.processing.AbstractProcessor
import spoon.reflect.declaration.CtClass
import java.util.function.Predicate

abstract class AbstractSpecificRecordProcessor(
  protected val context: SpoonContext
) : AbstractProcessor<CtClass<out SpecificRecordBase>>() {
  protected val logger: KLogger = context.logger

  protected val specificRecordRef by lazy {
    factory.typeReference<SpecificRecord>()
  }

  protected val specificRecordBaseRef by lazy {
    factory.typeReference<SpecificRecordBase>()
  }

  /**
   * Predicate that verifies that the given element is generated from avro schema.
   */
  protected val isGeneratedSpecificRecordClass: Predicate<CtClass<out SpecificRecordBase>> = Predicate {
    it.isSubtypeOf(specificRecordRef) && it.isSubtypeOf(specificRecordBaseRef)
  }

  protected fun hasRuntimeDependency(groupId: GroupId, artifactId: ArtifactId): Predicate<CtClass<out SpecificRecordBase>> = Predicate {
    context.hasRuntimeDependency.test(groupId, artifactId)
  }

  protected fun hasMetaDataType(type: RecordMetaDataType): Predicate<CtClass<out SpecificRecordBase>> = Predicate {
    type == context.metaData(it).type
  }

  protected val hasRevision : Predicate<CtClass<out SpecificRecordBase>> = Predicate {
    context.metaData(it).revision != null
  }

  override fun isToBeProcessed(candidate: CtClass<out SpecificRecordBase>): Boolean = isGeneratedSpecificRecordClass.test(candidate)

  override fun toString(): String = this::class.simpleName!!
}
