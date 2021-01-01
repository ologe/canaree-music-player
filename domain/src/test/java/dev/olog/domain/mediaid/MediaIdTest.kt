package dev.olog.domain.mediaid

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MediaIdTest {

    @Nested
    inner class Serialization {

        @Test
        fun `category with blank categoryValue, should throw`() {
            assertThrows(IllegalArgumentException::class.java) {
                MediaId.createCategoryValue(MediaIdCategory.FOLDERS, "").toString()
            }
        }

        @Test
        fun `category with integer categoryValue`() {
            serializeDeserialize(
                mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, "123"),
                expectedString = "FOLDERS#123#",
            )
        }

        @Test
        fun `category with string categoryValue`() {
            serializeDeserialize(
                mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, "string"),
                expectedString = "FOLDERS#string#",
            )
        }

        @Test
        fun `category with path categoryValue`() {
            serializeDeserialize(
                mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, "/storage/emulated/0"),
                expectedString = "FOLDERS#/storage/emulated/0#",
            )

            serializeDeserialize(
                mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, "/storage/a folder/emulated/123 folder"),
                expectedString = "FOLDERS#/storage/a folder/emulated/123 folder#",
            )
        }

        @Test
        fun `category with special chars categoryValue`() {
            val ascii = (32..127).map { it.toChar() }.joinToString("")

            serializeDeserialize(
                mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, ascii),
                expectedString = "FOLDERS#$ascii#",
            )
        }

        @Test
        fun leaf() {
            serializeDeserialize(
                mediaId = MediaId.songId(1),
                expectedString = "SONGS#all#1"
            )
            serializeDeserialize(
                mediaId = MediaId.playableItem(
                    MediaId.createCategoryValue(MediaIdCategory.FOLDERS, "category"),
                    1
                ),
                expectedString = "FOLDERS#category#1"
            )
        }

        @Test
        fun shuffle() {
            serializeDeserialize(
                mediaId = MediaId.shuffleId(),
                expectedString = "SONGS#all#-1#SHUFFLE"
            )
        }

        private fun serializeDeserialize(
            mediaId: MediaId,
            expectedString: String,
        ) {
            try {
                assertEquals(expectedString, mediaId.toString())
                assertEquals(mediaId, MediaId.fromString(mediaId.toString()))
            } catch (ex: Throwable) {
                throw RuntimeException("invalid $mediaId", ex)
            }
        }

    }

    @Test
    fun `test isType`() {
        for (category in MediaIdCategory.values()) {
            val mediaId = MediaId.createCategoryValue(category, "value")

            // folder
            assertEquals(category == MediaIdCategory.FOLDERS, mediaId.isFolder)

            // playlist
            assertEquals(category == MediaIdCategory.PLAYLISTS, mediaId.isPlaylist)
            assertEquals(category == MediaIdCategory.PODCASTS_PLAYLIST, mediaId.isPodcastPlaylist)
            assertEquals(
                category in listOf(
                    MediaIdCategory.PLAYLISTS,
                    MediaIdCategory.PODCASTS_PLAYLIST,
                ),
                mediaId.isAnyPlaylist
            )

            // track
            assertEquals(category == MediaIdCategory.SONGS, mediaId.isSongs)
            assertEquals(category == MediaIdCategory.PODCASTS, mediaId.isPodcast)

            // album
            assertEquals(category == MediaIdCategory.ALBUMS, mediaId.isAlbum)
            assertEquals(category == MediaIdCategory.PODCASTS_ALBUMS, mediaId.isPodcastAlbum)
            assertEquals(
                category in listOf(
                    MediaIdCategory.ALBUMS,
                    MediaIdCategory.PODCASTS_ALBUMS,
                ),
                mediaId.isAnyAlbum
            )

            // artist
            assertEquals(category == MediaIdCategory.ARTISTS, mediaId.isArtist)
            assertEquals(category == MediaIdCategory.PODCASTS_ARTISTS, mediaId.isPodcastArtist)
            assertEquals(
                category in listOf(
                    MediaIdCategory.ARTISTS,
                    MediaIdCategory.PODCASTS_ARTISTS,
                ),
                mediaId.isAnyArtist
            )

            // genre
            assertEquals(category == MediaIdCategory.GENRES, mediaId.isGenre)

            // podcast
            assertEquals(
                category in listOf(
                    MediaIdCategory.PODCASTS,
                    MediaIdCategory.PODCASTS_ALBUMS,
                    MediaIdCategory.PODCASTS_ARTISTS,
                    MediaIdCategory.PODCASTS_PLAYLIST,
                ),
                mediaId.isAnyPodcast
            )
        }
    }

}