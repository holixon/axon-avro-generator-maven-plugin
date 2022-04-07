package io.holixon.avro.maven.spoon

import io.holixon.avro.maven.spoon.ext.SpoonExt.annotation
import org.apache.avro.specific.SpecificRecordBase
import org.jmolecules.ddd.annotation.ValueObject
import spoon.reflect.declaration.CtAnnotation
import spoon.reflect.declaration.CtClass
import spoon.reflect.declaration.CtElement

class JMoleculesValueObjectAnnotationProcessor(context: SpoonContext) : AbstractSpecificRecordProcessor(context) {

  override fun isToBeProcessed(candidate: CtClass<out SpecificRecordBase>) = isGeneratedSpecificRecordClass
    .and(hasRuntimeDependency("org.jmolecules", "jmolecules-ddd"))
    .test(candidate)


  override fun process(element: CtClass<out SpecificRecordBase>) {
    val annotation: CtAnnotation<ValueObject> = factory.annotation<ValueObject>()
    element.addAnnotation<CtElement>(annotation)

    logger.info("""${element.qualifiedName}:  added $annotation""")
  }
}
