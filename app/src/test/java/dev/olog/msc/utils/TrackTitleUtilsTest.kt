package dev.olog.msc.utils

import org.junit.Assert
import org.junit.Test

class TrackTitleUtilsTest {

    @Test
    fun `test empty cases`(){
        val list = listOf(
                "",
                "some string () []",
                "(", ")", ")(", "((", "]]",
                "[", "]", "][", "[[", "]]",
                "{", "}", "}{", "{{", "}}",
                "some string )",
                "some string (",
                "some string ]",
                "some string ["
        )

        for (title in list) {
            val adjusted = TrackTitleUtils.adjust(title)

            Assert.assertEquals(title, adjusted.title) // same as initial
            Assert.assertFalse(adjusted.explicit)
            Assert.assertFalse(adjusted.remix)
        }
    }

    @Test
    fun `test string with parenthesis but no match cases`(){
        val title = "some string(best of)"

        val adjusted = TrackTitleUtils.adjust(title)

        Assert.assertEquals(title, adjusted.title) // same as initial
        Assert.assertFalse(adjusted.explicit)
        Assert.assertFalse(adjusted.remix)
    }

    @Test
    fun `test string with valid input and valid parenthesis`(){
        val list = listOf(
                "some string (video)",
                "some string (audio)",
                "some string (lyrics)",
                "some string (official)",
                "some string (hd)",
                "some string (VIDEO)",
                "some string (AUDIO)",
                "some string (LYRICS)",
                "some string (OFFICIAL)",
                "some string (HD)",
                "some string (Video)",
                "some string (Audio)",
                "some string (Lyrics)",
                "some string (Official)",
                "some string (Hd)"
        )

        for (title in list) {
            val adjusted = TrackTitleUtils.adjust(title)

            Assert.assertEquals("some string", adjusted.title)
            Assert.assertFalse(adjusted.explicit)
            Assert.assertFalse(adjusted.remix)
        }
    }

    @Test
    fun `test string with valid input and invalid parenthesis`(){
        val title = "some string (video"

        val adjusted = TrackTitleUtils.adjust(title)

        Assert.assertEquals(title, adjusted.title) // same as initial
        Assert.assertFalse(adjusted.explicit)
        Assert.assertFalse(adjusted.remix)
    }

    @Test
    fun `test string with valid input and invalid parenthesis 2`(){
        val title = "some string ((video"

        val adjusted = TrackTitleUtils.adjust(title)

        Assert.assertEquals(title, adjusted.title) // same as initial
        Assert.assertFalse(adjusted.explicit)
        Assert.assertFalse(adjusted.remix)
    }

    @Test
    fun `test string with valid input in parenthesis and remix only`(){
        val title = "some string (video)[remix]"

        val adjusted = TrackTitleUtils.adjust(title)

        Assert.assertEquals("some string", adjusted.title)
        Assert.assertFalse(adjusted.explicit)
        Assert.assertTrue(adjusted.remix)
    }

    @Test
    fun `test string with valid input in parenthesis and explicit only`(){
        val title = "some string (video) (explicit)"

        val adjusted = TrackTitleUtils.adjust(title)

        Assert.assertEquals("some string", adjusted.title)
        Assert.assertTrue(adjusted.explicit)
        Assert.assertFalse(adjusted.remix)
    }

    @Test
    fun `test string with valid input in parenthesis and remix and explicit`(){
        val title = "some string (video) (remix) (explicit)"

        val adjusted = TrackTitleUtils.adjust(title)

        Assert.assertEquals("some string", adjusted.title)
        Assert.assertTrue(adjusted.explicit)
        Assert.assertTrue(adjusted.remix)
    }

}