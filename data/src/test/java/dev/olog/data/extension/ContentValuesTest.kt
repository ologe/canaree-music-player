package dev.olog.data.extension

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class ContentValuesTest {

    @Test
    fun test() {
        val actual = ContentValues(
            "byte" to 1.toByte(),
            "short" to 2.toShort(),
            "int" to 3.toInt(),
            "long" to 4.toLong(),
            "float" to 5.1f,
            "double" to 6.1,
            "bool" to true,
            "byte_array" to byteArrayOf(1, 2, 3),
            "string" to "hello",
            "null" to null,
        )

        Assert.assertEquals(1.toByte(), actual.getAsByte("byte"))
        Assert.assertEquals(2.toShort(), actual.getAsShort("short"))
        Assert.assertEquals(3.toInt(), actual.getAsInteger("int"))
        Assert.assertEquals(4.toLong(), actual.getAsLong("long"))
        Assert.assertEquals(5.1f, actual.getAsFloat("float"), 0.000001f)
        Assert.assertEquals(6.1, actual.getAsDouble("double"), 0.000001)
        Assert.assertEquals(true, actual.getAsBoolean("bool"))
        Assert.assertEquals(true, Arrays.equals(byteArrayOf(1, 2, 3), actual.getAsByteArray("byte_array")))
        Assert.assertEquals("hello", actual.getAsString("string"))
        Assert.assertEquals(null, actual.get("null"))
    }

    @Test(expected = IllegalStateException::class)
    fun `test, should fail on invalid type`() {
        ContentValues("invalid" to Unit)
    }

}