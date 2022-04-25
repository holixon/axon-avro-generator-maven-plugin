package io.holixon.axon.avro.maven.spoon

import io.holixon.axon.avro.maven.spoon.ext.SpoonExt.addStringValue
import io.holixon.axon.avro.maven.spoon.ext.SpoonExt.annotation
import io.holixon.axon.avro.types.meta.RecordMetaDataType
import org.apache.avro.specific.SpecificRecordBase
import org.jmolecules.event.annotation.DomainEvent
import spoon.reflect.declaration.CtAnnotation
import spoon.reflect.declaration.CtClass
import spoon.reflect.declaration.CtElement

class JMoleculesDomainEventAnnotationProcessor(context: SpoonContext) : AbstractSpecificRecordProcessor(context) {

  override fun isToBeProcessed(candidate: CtClass<out SpecificRecordBase>) = isGeneratedSpecificRecordClass
    .and(hasRuntimeDependency("org.jmolecules", "jmolecules-events"))
    .and(hasMetaDataType(RecordMetaDataType.Event)).test(candidate)


  override fun process(element: CtClass<out SpecificRecordBase>) {
    val meta = context.metaData(element)

    val annotation: CtAnnotation<DomainEvent> = factory.annotation<DomainEvent>().apply {
      addStringValue("namespace", meta.namespace)
      addStringValue("name", meta.name)
    }

    element.addAnnotation<CtElement>(annotation)

    logger.info("""${element.qualifiedName}:  added $annotation""")
  }

}
