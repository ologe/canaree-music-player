package dev.olog.data.queries

import android.provider.MediaStore
import androidx.sqlite.db.SimpleSQLiteQuery
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.mediastore.artist.MediaStoreArtistEntity
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import dev.olog.data.mediastore.genre.MediaStoreGenreDao
import dev.olog.data.mediastore.genre.MediaStoreGenreEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

internal class GenreQueries @Inject constructor(
    private val dao: MediaStoreGenreDao,
    private val sortPrefs: SortPreferences,
) {

    fun getAll(): List<MediaStoreGenreEntity> {
        return dao.getAll()
    }

    fun observeAll(): Flow<List<MediaStoreGenreEntity>> {
        return dao.observeAll()
    }

    fun getById(id: Long): MediaStoreGenreEntity? {
        return dao.getById(id)
    }

    fun observeById(id: Long): Flow<MediaStoreGenreEntity?> {
        return dao.observeById(id)
    }

    fun observeRelatedArtists(id: Long): Flow<List<MediaStoreArtistEntity>> {
        return dao.observeRelatedArtists(id)
    }

    fun observeRecentlyAddedSongs(id: Long): Flow<List<MediaStoreAudioEntity>> {
        return dao.observeRecentlyAddedSongs(id)
    }

    fun getSongList(id: Long): List<MediaStoreAudioEntity> {
        val sort = sortPrefs.getDetailGenreSort()
        return dao.getTracks(SimpleSQLiteQuery(getSongListQuery(sort), arrayOf(id)))
    }

    fun observeSongList(id: Long): Flow<List<MediaStoreAudioEntity>> {
        return sortPrefs.observeDetailGenreSort()
            .flatMapLatest { sort ->
                dao.observeTracks(SimpleSQLiteQuery(getSongListQuery(sort), arrayOf(id)))
            }
    }

    private fun getSongListQuery(
        sort: SortEntity
    ): String {
        return """
            SELECT * FROM mediastore_audio
            WHERE genre_id = ?
            ORDER BY ${QueryUtils.songListSortOrder(sort, MediaStore.Audio.AudioColumns.TITLE)}
        """
    }

}