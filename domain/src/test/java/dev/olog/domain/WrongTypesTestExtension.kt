package dev.olog.domain

import org.junit.Assert

suspend fun <E : Enum<E>> Array<E>.catchIaeOnly(allowed: List<E>, builder: suspend (E) -> Unit) {
    val values = this.toList()

    for (value in values) {
        if (value in allowed) {
            continue
        }
        try {
            builder(value)
            Assert.fail("only $allowed is allow, instead was $value")
        } catch (ex: IllegalArgumentException) {

        }
    }
}