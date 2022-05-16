package io.holixon.axon.avro.generator.jmolecules

import org.apache.avro.specific.SpecificRecordBase
import spoon.processing.AbstractProcessor
import spoon.reflect.declaration.CtClass

class AddValueObjectAnnotationProcessor : AbstractProcessor<CtClass<out SpecificRecordBase>>(){
  override fun process(element: CtClass<out SpecificRecordBase>) {
    TODO("Not yet implemented")
  }

}
