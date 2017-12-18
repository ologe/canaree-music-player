package dev.olog.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import com.squareup.sqlbrite2.BriteContentResolver
import dev.olog.data.db.AppDatabase
import dev.olog.data.entity.GenreMostPlayedEntity
import dev.olog.data.mapper.extractId
import dev.olog.data.mapper.toGenre
import dev.olog.data.utils.FileUtils
import dev.olog.data.utils.assertBackgroundThread
import dev.olog.data.utils.getLong
import dev.olog.domain.entity.Genre
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.GenreGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.domain.interactor.data.GenreImagesUseCase
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdHelper
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenreRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
        private val rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase,
        private val genreImagesUseCase: GenreImagesUseCase

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
            ).mapToList {
                val genreSize = getGenreSize(it.getLong(BaseColumns._ID))
                it.toGenre(context, genreSize)
            }.map { it.sortedWith(compareBy { it.name.toLowerCase() }) }
            .toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()
            .replay(1)
            .refCount()

    private val songsMap : MutableMap<Long, Flowable<List<Song>>> = mutableMapOf()

    private fun getGenreSize(genreId: Long): Int {
        assertBackgroundThread()

        val cursor = contentResolver.query(MediaStore.Audio.Genres.Members.getContentUri("external", genreId),
                arrayOf("count(*)"), null, null, null)
        cursor.moveToFirst()
        val size = cursor.getInt(0)
        cursor.close()
        return size
    }

    override fun getAll(): Flowable<List<Genre>> {
        if (genreImagesUseCase.areImagesCreated().compareAndSet(false, true)){
            contentProviderObserver.firstOrError().flatMap { it.toFlowable()
                    .parallel()
                    .runOn(Schedulers.io())
                    .map { Pair(it, getSongListAlbumsId(it.id)) }
                    .map { (genre, albumsId) -> FileUtils.makeImages2(context, albumsId, "genre", "${genre.id}") }
                    .sequential()
                    .toList()
                    .doOnSuccess { contentResolver.notifyChange(MEDIA_STORE_URI, null) }
            }.doOnSuccess { genreImagesUseCase.setCreated() }
                    .subscribe({}, Throwable::printStackTrace)
        }

        return contentProviderObserver
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getByParam(playlistId: Long): Flowable<Genre> {
        return getAll().map { it.first { it.id == playlistId } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(genreId: Long): Flowable<List<Song>> {
        var flowable = songsMap[genreId]

        if (flowable == null){
            flowable = rxContentResolver.createQuery(
                    MediaStore.Audio.Genres.Members.getContentUri("external", genreId),
                    SONG_PROJECTION,
                    SONG_SELECTION,
                    SONG_SELECTION_ARGS,
                    SONG_SORT_ORDER,
                    false
            ).mapToList { it.extractId() }
                    .toFlowable(BackpressureStrategy.LATEST)
                    .flatMapSingle { ids -> songGateway.getAll().firstOrError().map { songs ->
                        ids.asSequence()
                                .map { id -> songs.firstOrNull { it.id == id } }
                                .filter { it != null }
                                .map { it!! }
                                .toList()
                    }}.distinctUntilChanged()
                    .replay(1)
                    .refCount()

            songsMap[genreId] = flowable
        }

        return flowable
    }

    private fun getSongListAlbumsId(genreId: Long): List<Long> {
        val result = mutableListOf<Long>()

        val cursor = contentResolver.query(
                MediaStore.Audio.Genres.Members.getContentUri("external", genreId),
                arrayOf(MediaStore.Audio.Genres.Members.ALBUM_ID), null, null, null)
        while (cursor.moveToNext()){
            result.add(cursor.getLong(0))
        }
        cursor.close()
        return result
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getMostPlayed(mediaId: String): Flowable<List<Song>> {
        val playlistId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
        return mostPlayedDao.getAll(playlistId, songGateway.getAll())
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