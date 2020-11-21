package dev.olog.test.shared

import org.assertj.core.api.*

fun assertThat(value: Boolean): AbstractBooleanAssert<*> = Assertions.assertThat(value)
fun assertThat(value: Int): AbstractIntegerAssert<*> = Assertions.assertThat(value)
fun assertThat(value: Long): AbstractLongAssert<*> = Assertions.assertThat(value)
fun assertThat(value: Float): AbstractFloatAssert<*> = Assertions.assertThat(value)
fun assertThat(value: Double): AbstractDoubleAssert<*> = Assertions.assertThat(value)

fun <T> assertThat(value: T): ObjectAssert<*> = Assertions.assertThat(value)