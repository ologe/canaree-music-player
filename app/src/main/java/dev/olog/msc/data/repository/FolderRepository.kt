package dev.olog.msc.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.FolderMostPlayedEntity
import dev.olog.msc.data.mapper.toFolder
import dev.olog.msc.domain.entity.Folder
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.FolderGateway
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
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private val MEDIA_STORE_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

@Singleton
class FolderRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase,
        private val imagesCreator: ImagesCreator

): BaseRepository<Folder, String>(), FolderGateway {

    private val mostPlayedDao = appDatabase.folderMostPlayedDao()

    override fun queryAllData(): Observable<List<Folder>> {
        return songGateway.getAll()
                .map { songList ->
                    songList.asSequence()
                            .distinctBy { it.folderPath }
                            .map { song ->
                                song.toFolder(context,
                                        songList.count { it.folderPath == song.folderPath }) // count song for all folder
                            }.sortedBy { it.title }
                            .toList()
                }.onErrorReturn { listOf() }
                .doOnNext { imagesCreator.subscribe(createImages()) }
                .doOnTerminate { imagesCreator.unsubscribe() }
    }

    override fun getByParamImpl(list: List<Folder>, param: String): Folder {
        return list.first { it.path == param }
    }

    override fun createImages() : Single<Any>{
        return songGateway.getAll()
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { it.groupBy { it.folderPath } }
                .flattenAsFlowable { it.entries }
                .parallel()
                .runOn(Schedulers.io())
                .map { entry -> try {
                    runBlocking { makeImage(this@FolderRepository.context, entry).await() }
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

    private fun makeImage(context: Context, map: Map.Entry<String, List<Song>>) : Deferred<Boolean> = async {
        val folderName = ImagesFolderUtils.getFolderName(ImagesFolderUtils.FOLDER)
        val normalizedPath = map.key.replace(File.separator, "")
        MergedImagesCreator.makeImages(context, map.value, folderName, normalizedPath)
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(path: String): Observable<List<Song>> {
        val observable = songGateway.getAll().map {
            it.asSequence().filter { it.folderPath == path}.toList()
        }.distinctUntilChanged()

        return observable.emitThenDebounce()
    }

    override fun getMostPlayed(mediaId: MediaId): Observable<List<Song>> {
        val folderPath = mediaId.categoryValue
        val observable = mostPlayedDao.getAll(folderPath, songGateway.getAll())

        return observable.emitThenDebounce()
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable {
        val songId = mediaId.leaf!!
        return songGateway.getByParam(songId)
                .flatMapCompletable { song ->
                    CompletableSource { mostPlayedDao.insertOne(FolderMostPlayedEntity(0, song.id, song.folderPath)) }
                }
    }

    override fun getAllUnfiltered(): Observable<List<Folder>> {
        return songGateway.getAllUnfiltered()
                .map { songList ->
                    songList.asSequence()
                            .distinctBy { it.folderPath }
                            .map { song ->
                                song.toFolder(context,
                                        songList.count { it.folderPath == song.folderPath }) // count song for all folder
                            }.sortedBy { it.title }
                            .toList()
                }
    }
}