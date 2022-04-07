package io.holixon.avro.maven.spoon

import io.holixon.avro.maven.spoon.ext.SpoonExt.addStringValue
import io.holixon.avro.maven.spoon.ext.SpoonExt.annotation
import org.apache.avro.specific.SpecificRecordBase
import org.axonframework.serialization.Revision
import spoon.reflect.declaration.CtAnnotation
import spoon.reflect.declaration.CtClass
import spoon.reflect.declaration.CtElement

/**
 * Adds a `Revision("...")` annotation to generated SpecificRecord.
 */
class AxonRevisionAnnotationProcessor(context: SpoonContext) : AbstractSpecificRecordProcessor(context) {

  override fun process(type: CtClass<out SpecificRecordBase>) {
    val meta = context.metaData(type)

    val annotation: CtAnnotation<Revision> = factory.annotation<Revision>().apply {
      addStringValue("value", meta.revision!!)
    }
    type.addAnnotation<CtElement>(annotation)

    logger.info("""${type.qualifiedName}:  added $annotation""")
  }

  override fun isToBeProcessed(candidate: CtClass<out SpecificRecordBase>): Boolean = isGeneratedSpecificRecordClass
    .and(hasRuntimeDependency("org.axonframework", "axon-messaging"))
    .and(hasRevision).test(candidate)
}
