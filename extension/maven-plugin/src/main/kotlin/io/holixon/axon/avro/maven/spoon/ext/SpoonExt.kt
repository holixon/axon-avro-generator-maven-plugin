package io.holixon.axon.avro.maven.spoon.ext

import spoon.reflect.declaration.CtAnnotation
import spoon.reflect.factory.Factory
import spoon.reflect.reference.CtTypeReference

object SpoonExt {

  inline fun <reified T : Any> Factory.typeReference(): CtTypeReference<T> = this.createCtTypeReference<T>(T::class.java)

  inline fun <reified T : Annotation> Factory.annotation(): CtAnnotation<T> {
    val codeRef = Code().createCtTypeReference<T>(T::class.java)
    return Code().createAnnotation<T>(codeRef)
  }

  inline fun <reified T : Annotation> CtAnnotation<T>.addStringValue(name: String, value: String) = this.addValue<CtAnnotation<T>>(name, value)
}
