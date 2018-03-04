package dev.olog.msc.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.GenreMostPlayedEntity
import dev.olog.msc.data.mapper.extractId
import dev.olog.msc.data.mapper.toGenre
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.emitThenDebounce
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Observable
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI
private val PROJECTION = arrayOf(
        MediaStore.Audio.Genres._ID,
        MediaStore.Audio.Genres.NAME
)
private val SELECTION: String? = null
private val SELECTION_ARGS: Array<String>? = null
private const val SORT_ORDER = "lower(${MediaStore.Audio.Genres.NAME})"

private val SONG_PROJECTION = arrayOf(BaseColumns._ID)
private val SONG_SELECTION = null
private val SONG_SELECTION_ARGS: Array<String>? = null
private const val SONG_SORT_ORDER = "lower(${MediaStore.Audio.Genres.Members.TITLE})"

class GenreRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
        private val rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase

) : GenreGateway {

    private val mostPlayedDao = appDatabase.genreMostPlayedDao()

    private fun queryAllData(): Observable<List<Genre>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, PROJECTION, SELECTION,
                SELECTION_ARGS, SORT_ORDER, true
        ).mapToList {
            val id = it.extractId()
            val uri = MediaStore.Audio.Genres.Members.getContentUri("external", id)
            val size = CommonQuery.getSize(contentResolver, uri)
            it.toGenre(context, size)
        }.onErrorReturn { listOf() }
    }

    private val cachedData = queryAllData()
            .replay(1)
            .refCount()

    override fun getAll(): Observable<List<Genre>> {
        return cachedData.emitThenDebounce()
    }

    override fun getAllNewRequest(): Observable<List<Genre>> {
        return queryAllData()
    }

    override fun getByParam(param: Long): Observable<Genre> {
        return cachedData.map { it.first { it.id == param } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(genreId: Long): Observable<List<Song>> {
        val uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId)

        val observable = rxContentResolver.createQuery(
                uri,SONG_PROJECTION,
                SONG_SELECTION,
                SONG_SELECTION_ARGS,
                SONG_SORT_ORDER,
                false
        ).mapToList { it.extractId() }
                .flatMapSingle { ids -> songGateway.getAll().firstOrError().map { songs ->
                    ids.asSequence()
                            .mapNotNull { id -> songs.firstOrNull { it.id == id } }
                            .toList()
                }}.distinctUntilChanged()

        return observable.emitThenDebounce()
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getMostPlayed(mediaId: MediaId): Observable<List<Song>> {
        val genreId = mediaId.categoryValue.toLong()
        val observable = mostPlayedDao.getAll(genreId, songGateway.getAll())
        return observable.emitThenDebounce()
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable {
        val songId = mediaId.leaf!!
        val genreId = mediaId.categoryValue.toLong()
        return songGateway.getByParam(songId)
                .flatMapCompletable { song ->
                    CompletableSource { mostPlayedDao.insertOne(GenreMostPlayedEntity(0, song.id, genreId)) }
                }
    }

}