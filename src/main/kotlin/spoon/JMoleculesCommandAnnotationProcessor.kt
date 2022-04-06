package io.holixon.avro.maven.spoon

import io.holixon.avro.maven.avro.DDDType
import io.holixon.avro.maven.spoon.ext.addStringValue
import io.holixon.avro.maven.spoon.ext.annotation
import org.apache.avro.specific.SpecificRecordBase
import org.jmolecules.architecture.cqrs.annotation.Command
import spoon.reflect.declaration.CtAnnotation
import spoon.reflect.declaration.CtClass
import spoon.reflect.declaration.CtElement

class JMoleculesCommandAnnotationProcessor(context: SpoonContext) : AbstractSpecificRecordProcessor(context) {

  override fun isToBeProcessed(candidate: CtClass<out SpecificRecordBase>) = isGeneratedSpecificRecordClass
    .and(hasRuntimeDependency("org.jmolecules", "jmolecules-cqrs-architecture"))
    .and(hasMetaDataType(DDDType.command)).test(candidate)

  override fun process(element: CtClass<out SpecificRecordBase>) {
    val meta = context.metaData(element)

    val annotation: CtAnnotation<Command> = factory.annotation<Command>().apply {
      addStringValue("namespace", meta.namespace)
      addStringValue("name", meta.name)
    }

    element.addAnnotation<CtElement>(annotation)

    logger.info("""${element.qualifiedName}:  added $annotation""")
  }
}
