package io.holixon.avro.maven.spoon

import io.holixon.avro.maven.spoon.SpoonExt.addStringValue
import io.holixon.avro.maven.spoon.SpoonExt.annotation
import io.holixon.avro.maven.spoon.SpoonExt.typeReference
import org.apache.avro.specific.SpecificRecord
import org.apache.avro.specific.SpecificRecordBase
import org.axonframework.serialization.Revision
import spoon.processing.AbstractProcessor
import spoon.reflect.declaration.CtAnnotation
import spoon.reflect.declaration.CtClass
import spoon.reflect.declaration.CtElement

/**
 * Adds a `Revision("...")` annotation to generated SpecificRecord.
 */
class AxonRevisionAnnotationProcessor(
  private val context: SpoonContext
) : AbstractProcessor<CtClass<out SpecificRecordBase>>() {

  private val log = context.logger

  private val specificRecordRef by lazy {
    factory.typeReference<SpecificRecord>()
  }
  private val specificRecordBaseRef by lazy {
    factory.typeReference<SpecificRecordBase>()
  }

  override fun isToBeProcessed(candidate: CtClass<out SpecificRecordBase>): Boolean {
    return candidate.isSubtypeOf(specificRecordRef)
      && candidate.isSubtypeOf(specificRecordBaseRef)
  }

  override fun process(type: CtClass<out SpecificRecordBase>) {

    log.info { "processing: ${type.qualifiedName}" }
    log.info { "processing2: ${type.getAllMetadata()}" }

    val meta = context.metaData(type)
    log.info { "processing3: ${meta}" }

    meta.revision?.let { revision ->
      val annotation: CtAnnotation<Revision> = factory.annotation()
      annotation.addStringValue("value", revision)

      type.addAnnotation<CtElement>(annotation)

      log.info("""${type.qualifiedName}:  added @Revision("$revision")""")
    }
  }

  override fun toString(): String {
    return "AxonRevisionAnnotationProcessor"
  }
}
