package dev.olog.data.playlist.persister

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class PersistedPlaylistTest {

    companion object {
        private val file = File("file.m3u")
    }

    private val playlist = PersistedPlaylist(file)

    @Before
    fun setup() {
        playlist.clear()
    }

    @Test
    fun `test add`() {
        playlist.add("/abc")
        playlist.add("abc/def")

        Assert.assertEquals(
            listOf("/abc", "abc/def"),
            playlist.asList()
        )
    }

    @Test
    fun `test addMultiple`() {
        playlist.addMultiple(listOf("/abc", "abc/def"))

        Assert.assertEquals(
            listOf("/abc", "abc/def"),
            playlist.asList()
        )
    }

    @Test
    fun `test removeAt`() {
        playlist.add("/abc")
        playlist.add("abc/def")
        playlist.add("def")
        playlist.add("/def")
        playlist.add("def/")

        // remove middle
        playlist.removeAt(2)
        Assert.assertEquals(
            listOf("/abc", "abc/def", "/def", "def/"),
            playlist.asList()
        )

        // remove out of bound, > size, fallback to last
        playlist.removeAt(playlist.asList().size + 1)
        Assert.assertEquals(
            listOf("/abc", "abc/def", "/def"),
            playlist.asList()
        )

        // remove out of bound, < 0, fallback to first
        playlist.removeAt(-1)
        Assert.assertEquals(
            listOf("abc/def", "/def"),
            playlist.asList()
        )
    }

    @Test
    fun `test removeMultiple`() {
        playlist.add("/abc")
        playlist.add("abc/def")
        playlist.add("def")
        playlist.add("def")

        playlist.removeMultiple(listOf("/abc", "def"))

        Assert.assertEquals(
            listOf("abc/def"),
            playlist.asList()
        )
    }

    @Test
    fun `test move`() {
        playlist.addMultiple(
            listOf(
                "/abc",
                "abc/def",
                "def",
                "def/",
            )
        )

        playlist.move(1, 2)

        Assert.assertEquals(
            listOf(
                "/abc",
                "def",
                "abc/def",
                "def/",
            ),
            playlist.asList()
        )

        playlist.move(0, 3)

        Assert.assertEquals(
            listOf(
                "def/",
                "def",
                "abc/def",
                "/abc",
            ),
            playlist.asList()
        )

        playlist.move(-1, playlist.asList().size + 1)

        Assert.assertEquals(
            listOf(
                "/abc",
                "def",
                "abc/def",
                "def/",
            ),
            playlist.asList()
        )

        playlist.move(playlist.asList().size + 1, -1)

        Assert.assertEquals(
            listOf(
                "def/",
                "def",
                "abc/def",
                "/abc",
            ),
            playlist.asList()
        )
    }

    @Test
    fun `test clear`() {
        playlist.add("abc")
        playlist.add("def")

        playlist.clear()

        Assert.assertEquals(
            emptyList<String>(),
            playlist.asList()
        )
    }

}