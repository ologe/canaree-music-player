package dev.olog.msc.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import dev.olog.msc.dagger.ApplicationContext
import dev.olog.msc.data.FileUtils
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.GenreMostPlayedEntity
import dev.olog.msc.data.mapper.extractId
import dev.olog.msc.data.mapper.toGenre
import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.getLong
import dev.olog.shared_android.ImagesFolderUtils
import dev.olog.shared_android.assertBackgroundThread
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenreRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
        private val rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase,
        imagesCreator: ImagesCreator

) : GenreGateway {

    companion object {
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
            }
            .onErrorReturn { listOf() }
            .map { it.sortedWith(compareBy { it.name.toLowerCase() }) }
            .toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()
            .doOnNext { imagesCreator.subscribe(createImages()) }
            .replay(1)
            .refCount()
            .doOnTerminate { imagesCreator.unsubscribe() }

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

    override fun createImages() : Single<Any> {
        return contentProviderObserver.firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flattenAsFlowable { it }
                .parallel()
                .runOn(Schedulers.io())
                .map { Pair(it, getSongListAlbumsId(it.id)) }
                .map { (genre, albumsId) -> try {
                    runBlocking { makeImage(this@GenreRepository.context, genre, albumsId).await() }
                } catch (ex: Exception){/*amen*/}
                }.sequential()
                .toList()
                .map { it.contains(true) }
                .onErrorReturnItem(false)
                .doOnSuccess { created ->
                    if (created) {
                        contentResolver.notifyChange(MEDIA_STORE_URI, null)
                    }
                }.map { Unit }
    }

    private fun makeImage(context: Context, genre: Genre, albumsId: List<Long>) : Deferred<Boolean> = async {
        val folderName = ImagesFolderUtils.getFolderName(ImagesFolderUtils.GENRE)
        FileUtils.makeImages2(context, albumsId, folderName, "${genre.id}")
    }


    override fun getAll(): Flowable<List<Genre>> = contentProviderObserver

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
    override fun getMostPlayed(mediaId: MediaId): Flowable<List<Song>> {
        val genreId = mediaId.categoryValue.toLong()
        return mostPlayedDao.getAll(genreId, songGateway.getAll())
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