package dev.olog.data.repository

import dev.olog.core.entity.track.*

internal object MockData {

    private const val SONG_LIST_SIZE = 100L
    private const val LIST_SIZE = 30L

    fun folders(): List<Folder> {
        return (0L until LIST_SIZE)
            .map { Folder(it, "Folder", "/storage/emulated/0/$it", it.toInt()) }

    }

    fun autoPlaylist(): List<Playlist> {
        return listOf(
            Playlist(0, "Last added", 0, false),
            Playlist(1, "Favorites", 1, false),
            Playlist(2, "History", 2, false)
        )
    }

    fun playlist(podcast: Boolean): List<Playlist> {
        return (0L until LIST_SIZE)
            .map { Playlist(it, "Playlist $it", it.toInt(), podcast) }

    }

    fun songs(podcast: Boolean): List<Song> {
        return (0L until SONG_LIST_SIZE)
            .map { index ->
                Song(
                    index, index, index,
                    "An awesome song", "An awesome artist", "An awesome artist",
                    "An awesome album", 100_0000,
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    "/storage/emulated/0",
                    1, index.toInt(),
                    podcast,
                    "display name"
                )
            }
    }

    fun album(): List<Album> {
        return (0L until LIST_SIZE)
            .map {
                Album(
                    it, it, "An awesome album", "An awesome artist", "An awesome artist",
                    it.toInt(), false
                )
            }

    }

    fun artist(podcast: Boolean): List<Artist> {
        return (0L until LIST_SIZE)
            .map { Artist(it, "An awesome artist", "An awesome artist", it.toInt(), podcast) }

    }

    fun genre(): List<Genre> {
        return (0L until LIST_SIZE)
            .map { Genre(it, "Genre", it.toInt()) }

    }

}