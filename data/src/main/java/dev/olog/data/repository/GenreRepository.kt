package dev.olog.data.repository

import android.content.ContentResolver
import android.provider.BaseColumns
import android.provider.MediaStore
import com.squareup.sqlbrite2.BriteContentResolver
import dev.olog.data.db.AppDatabase
import dev.olog.data.entity.GenreMostPlayedEntity
import dev.olog.data.mapper.extractId
import dev.olog.data.mapper.toGenre
import dev.olog.domain.entity.Genre
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.GenreGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.shared.MediaIdHelper
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenreRepository @Inject constructor(
        private val contentResolver: ContentResolver,
        private val rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase

) : GenreGateway {

    companion object {
        private val MEDIA_STORE_URI = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI
        private val PROJECTION = arrayOf(
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME
        )
        private val SELECTION: String? = null
        private val SELECTION_ARGS: Array<String>? = null
        private val SORT_ORDER = MediaStore.Audio.Genres.DEFAULT_SORT_ORDER

        private val SONG_PROJECTION = arrayOf(BaseColumns._ID)
        private val SONG_SELECTION = null
        private val SONG_SELECTION_ARGS: Array<String>? = null
        private val SONG_SORT_ORDER = MediaStore.Audio.Genres.Members.DEFAULT_SORT_ORDER
    }

    private val mostPlayedDao = appDatabase.genreMostPlayedDao()

    private val contentProviderObserver = rxContentResolver
            .createQuery(
                    MEDIA_STORE_URI,
                    PROJECTION,
                    SELECTION,
                    SELECTION_ARGS,
                    SORT_ORDER,
                    false
            ).mapToList { it.toGenre() }
            .map { it.sortedWith(compareBy { it.name.toLowerCase() }) }
            .toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()
            .replay(1)
            .refCount()

    override fun getAll(): Flowable<List<Genre>> = contentProviderObserver

    override fun getByParam(param: Long): Flowable<Genre> {
        return getAll().flatMapSingle { it.toFlowable()
                .filter { it.id == param }
                .firstOrError()
        }
    }

    override fun observeSongListByParam(param: Long): Flowable<List<Song>> {
        return rxContentResolver.createQuery(
                MediaStore.Audio.Genres.Members.getContentUri("external", param),
                SONG_PROJECTION,
                SONG_SELECTION,
                SONG_SELECTION_ARGS,
                SONG_SORT_ORDER,
                false

        ).mapToList { it.extractId() }
                .toFlowable(BackpressureStrategy.LATEST)
                .flatMapSingle { it.toFlowable()
                        .flatMapMaybe { songId -> songGateway.getAll()
                                .flatMapIterable { it }
                                .filter { it.id == songId }
                                .firstElement()
                        }.toList()
                }
    }

    override fun getMostPlayed(param: String): Flowable<List<Song>> {
        return mostPlayedDao.getAll(MediaIdHelper.extractCategoryValue(param).toLong(), songGateway.getAll())
    }

    override fun insertMostPlayed(mediaId: String): Completable {
        val songId = MediaIdHelper.extractLeaf(mediaId).toLong()
        val genreId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
        return songGateway.getByParam(songId)
                .flatMapCompletable { song ->
                    CompletableSource { mostPlayedDao.insertOne(GenreMostPlayedEntity(0, song.id, genreId)) }
                }
    }
}