package io.holixon.avro.maven

import org.apache.maven.plugin.logging.Log
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

fun Logger.maven() = MavenSlf4jLogger(this)

class MavenSlf4jLogger(private val logger: Logger) : Log {

  constructor(clazz : KClass<*>) : this(LoggerFactory.getLogger(clazz.java))

  override fun isDebugEnabled(): Boolean = logger.isDebugEnabled
  override fun debug(msg: CharSequence) = logger.debug("$msg")
  override fun debug(msg: CharSequence, exception: Throwable) = logger.debug("$msg", exception)
  override fun debug(exception: Throwable) = debug("", exception)

  override fun isInfoEnabled(): Boolean = logger.isInfoEnabled
  override fun info(msg: CharSequence) = logger.info("$msg")
  override fun info(msg: CharSequence, exception: Throwable) = logger.info("$msg", exception)
  override fun info(exception: Throwable) = info("", exception)

  override fun isWarnEnabled(): Boolean = logger.isWarnEnabled
  override fun warn(msg: CharSequence) = logger.warn("$msg")
  override fun warn(msg: CharSequence, exception: Throwable) = logger.warn("$msg", exception)
  override fun warn(exception: Throwable) = warn("", exception)

  override fun isErrorEnabled(): Boolean = logger.isErrorEnabled
  override fun error(msg: CharSequence) = logger.error("$msg")
  override fun error(msg: CharSequence, exception: Throwable) = logger.error("$msg", exception)
  override fun error(exception: Throwable) = warn("", exception)
}


fun log(type: KClass<*>): Log {
  val logger = LoggerFactory.getLogger(type::class.java)

  return object : Log {
    override fun isDebugEnabled(): Boolean = logger.isDebugEnabled
    override fun debug(msg: CharSequence) = logger.debug("$msg")
    override fun debug(msg: CharSequence, exception: Throwable) = logger.debug("$msg", exception)
    override fun debug(exception: Throwable) = debug("", exception)

    override fun isInfoEnabled(): Boolean = logger.isInfoEnabled
    override fun info(msg: CharSequence) = logger.info("$msg")
    override fun info(msg: CharSequence, exception: Throwable) = logger.info("$msg", exception)
    override fun info(exception: Throwable) = info("", exception)

    override fun isWarnEnabled(): Boolean = logger.isWarnEnabled
    override fun warn(msg: CharSequence) = logger.warn("$msg")
    override fun warn(msg: CharSequence, exception: Throwable) = logger.warn("$msg", exception)
    override fun warn(exception: Throwable) = warn("", exception)

    override fun isErrorEnabled(): Boolean = logger.isErrorEnabled
    override fun error(msg: CharSequence) = logger.error("$msg")
    override fun error(msg: CharSequence, exception: Throwable) = logger.error("$msg", exception)
    override fun error(exception: Throwable) = warn("", exception)
  }
}

internal class AxonAvroGeneratorMojoTest {

}
