package io.holixon.axon.avro.generator.jmolecules

import org.apache.avro.specific.SpecificRecordBase
import spoon.reflect.declaration.CtClass
import java.util.function.Predicate

object JMoleculesPredicates {

  fun interface IsActiveCqrsArchitecture : Predicate<CtClass<out SpecificRecordBase>>

  fun interface IsActiveDDD : Predicate<CtClass<out SpecificRecordBase>>


}
