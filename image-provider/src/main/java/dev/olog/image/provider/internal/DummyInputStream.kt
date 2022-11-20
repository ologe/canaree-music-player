package dev.olog.image.provider.internal

import java.io.ByteArrayInputStream
import java.io.File

internal class DummyInputStream : ByteArrayInputStream(VALUE_BYTES) {

    companion object {
        val VALUE_BYTES = "dummy".toByteArray()
    }

}

internal fun File.isDummyStream(): Boolean {
    return length().toInt() == DummyInputStream.VALUE_BYTES.size &&
        readBytes().contentEquals(DummyInputStream.VALUE_BYTES)
}