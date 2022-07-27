package dev.olog.data

import android.provider.MediaStore
import dev.olog.data.mediastore.MediaStoreAudioEntity

data class TestArtist(
    val id: String,
    val name: String,
    val isPodcast: Boolean,
    val albums: List<TestAlbum>,
) {
    constructor(
        id: String,
        name: String,
        isPodcast: Boolean,
        vararg albums: TestAlbum
    ) : this(id, name, isPodcast, albums.toList())
}

data class TestAlbum(
    val id: String,
    val title: String,
    val albumArtist: String,
    val discNumber: Int,
    val songs: List<TestSong>,
) {
    constructor(
        id: String,
        title: String,
        albumArtist: String,
        discNumber: Int,
        vararg songs: TestSong
    ) : this(id, title, albumArtist, discNumber, songs.toList())
}

data class TestSong(
    val id: String,
    val title: String,
    val duration: Long,
    val dateAdded: Long,
    val trackNumber: Int,
)

fun List<TestArtist>.flatten(): List<MediaStoreAudioEntity> {
    return map { artist ->
        artist.albums.map { album ->
            album.songs.map { song ->
                emptyMediaStoreAudioEntity(
                    id = song.id,
                    artistId = artist.id,
                    albumId = album.id,
                    title = song.title,
                    artist = artist.name,
                    album = album.title,
                    albumArtist = album.albumArtist,
                    duration = song.duration,
                    dateAdded = song.dateAdded,
                    isPodcast = artist.isPodcast,
                    discNumber = album.discNumber,
                    trackNumber = song.trackNumber,
                )
            }
        }
    }.flatten().flatten()
}

object TestData {

    fun items(isPodcast: Boolean) = listOf(
        TestArtist(
            id = "1",
            name = MediaStore.UNKNOWN_STRING,
            isPodcast = isPodcast,
            TestAlbum(
                id = "10",
                title = MediaStore.UNKNOWN_STRING,
                albumArtist = MediaStore.UNKNOWN_STRING,
                discNumber = 0,
                TestSong(
                    id = "100",
                    title = "aaa track",
                    duration = 10_000,
                    dateAdded = 10_001,
                    trackNumber = 0,
                ),
                TestSong(
                    id = "101",
                    title = "zzz track",
                    duration = -10_000,
                    dateAdded = -10_001,
                    trackNumber = 0,
                )
            ),
        ),
        TestArtist(
            id = "2",
            name = "dEa artist 1",
            isPodcast = isPodcast,
            TestAlbum(
                id = "20",
                title = "déh album 1",
                albumArtist = "déh album artist 1",
                discNumber = 1,
                TestSong(
                    id = "200",
                    title = "dec",
                    duration = 200,
                    dateAdded = 201,
                    trackNumber = 1,
                ),
                TestSong(
                    id = "201",
                    title = "dèb",
                    duration = 200,
                    dateAdded = 201,
                    trackNumber = 2,
                ),
            ),
            TestAlbum(
                id = "21",
                title = "dec album 2",
                albumArtist = "dec album artist 2",
                discNumber = 2,
                TestSong(
                    id = "210",
                    title = "déh",
                    duration = 200,
                    dateAdded = 201,
                    trackNumber = 1,
                ),
                TestSong(
                    id = "211",
                    title = "dEa",
                    duration = 200,
                    dateAdded = 201,
                    trackNumber = 2,
                ),
                TestSong(
                    id = "212",
                    title = "ggg",
                    duration = 210,
                    dateAdded = 211,
                    trackNumber = 3,
                ),
            ),
        ),
        TestArtist(
            id = "3",
            name = "déh artist 2",
            isPodcast = isPodcast,
            TestAlbum(
                id = "30",
                title = "dEa another album",
                albumArtist = "dEa another artist album",
                discNumber = 0,
                TestSong(
                    id = "300",
                    title = "dEG",
                    duration = 300,
                    dateAdded = 301,
                    trackNumber = 0,
                )
            )
        ),
        TestArtist(
            id = "4",
            name = "dec artist 3",
            isPodcast = isPodcast,
            TestAlbum(
                id = "40",
                title = "dEg artist 3 album",
                albumArtist = "dEg artist 3 album artist",
                discNumber = 1,
                TestSong(
                    id = "400",
                    title = "hello",
                    duration = 200,
                    dateAdded = 201,
                    trackNumber = 1,
                )
            )
        )
    ).flatten()

}