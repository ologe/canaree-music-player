package dev.olog.domain

import dev.olog.domain.mediaid.MediaIdModifier
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class MediaIdModifierTest {

    @Test
    fun `test findOrNull`() {
        val modifiers = MediaIdModifier.values()

        // existing values should success
        for (modifier in modifiers) {
            assertEquals(
                modifier,
                MediaIdModifier.findOrNull(modifier.toString())
            )
        }

        // random value should return null
        assertEquals(
            null,
            MediaIdModifier.findOrNull("random")
        )
    }

}