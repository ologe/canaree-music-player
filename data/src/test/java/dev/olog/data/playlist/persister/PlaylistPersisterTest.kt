package dev.olog.data.playlist.persister

import org.junit.Assert
import org.junit.Test

class PlaylistPersisterTest {

    @Test
    fun `test resolvePersister`() {
        val map = mapOf(
            "audio/mpegurl" to M3uPlaylistPersister::class.java,
            "audio/x-mpegurl" to M3uPlaylistPersister::class.java,
            "application/vnd.apple.mpegurl" to M3uPlaylistPersister::class.java,
            "application/x-mpegurl" to M3uPlaylistPersister::class.java,
            "audio/x-scpls" to PlsPlaylistPersister::class.java,
            "application/vnd.ms-wpl" to WplPlaylistPersister::class.java,
            "video/x-ms-asf" to WplPlaylistPersister::class.java,
            "application/xspf+xml" to XspfPlaylistPersister::class.java,
            "random" to null
        )

        for ((mimeType, v) in map) {
            val actual = PlaylistPersister.resolvePersister(mimeType)
            Assert.assertEquals(map[mimeType], actual?.let { it::class.java })
        }
    }

}