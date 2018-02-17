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
import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.img.MergedImagesCreator
import dev.olog.msc.utils.k.extension.emitThenDebounce
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

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

@Singleton
class GenreRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
        private val rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase,
        private val imagesCreator: ImagesCreator

) : BaseRepository<Genre, Long>(), GenreGateway {

    private val mostPlayedDao = appDatabase.genreMostPlayedDao()

    override fun queryAllData(): Observable<List<Genre>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, PROJECTION, SELECTION,
                SELECTION_ARGS, SORT_ORDER, false
        ).mapToList {
            val id = it.extractId()
            val uri = MediaStore.Audio.Genres.Members.getContentUri("external", id)
            val size = CommonQuery.getSize(contentResolver, uri)
            it.toGenre(context, size)
        }.onErrorReturn { listOf() }
                .doOnNext { imagesCreator.subscribe(createImages()) }
                .doOnTerminate { imagesCreator.unsubscribe() }
    }

    override fun createImages() : Single<Any> {
        return getAll().firstOrError()
                .flattenAsFlowable { it }
                .parallel()
                .runOn(Schedulers.io())
                .map {
                    val uri = MediaStore.Audio.Genres.Members.getContentUri("external", it.id)
                    Pair(it, CommonQuery.extractAlbumIdsFromSongs(contentResolver, uri))
                }
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
        MergedImagesCreator.makeImages2(context, albumsId, folderName, "${genre.id}")
    }

    override fun getByParamImpl(list: List<Genre>, param: Long): Genre {
        return list.first { it.id == param }
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