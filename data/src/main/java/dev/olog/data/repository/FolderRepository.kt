package dev.olog.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import dev.olog.data.db.AppDatabase
import dev.olog.data.entity.FolderMostPlayedEntity
import dev.olog.data.mapper.toFolder
import dev.olog.data.utils.FileUtils
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaId
import dev.olog.shared.flatMapGroup
import dev.olog.shared.unsubscribe
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

private val MEDIA_STORE_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

@Singleton
class FolderRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase

): FolderGateway {

    private val mostPlayedDao = appDatabase.folderMostPlayedDao()

    private var imageDisposable : Disposable? = null

    private val songMap : MutableMap<String, Flowable<List<Song>>> = mutableMapOf()

    private val creatingImages = AtomicBoolean(false)

    private val listObservable = songGateway.getAll()
            .flatMapGroup { distinct { it.folderPath } }
            .flatMapSingle { songsToFolder -> songGateway.getAll().firstOrError()
                    .map { songList ->
                        songsToFolder.map { song -> song.toFolder(context,
                                songList.count { it.folderPath == song.folderPath })
                        }.sortedBy { it.title.toLowerCase() }
                    }
            }.distinctUntilChanged()
            .doOnNext { subscribeToImageCreation() }
            .replay(1)
            .refCount()
            .doOnTerminate {
                creatingImages.compareAndSet(true, false)
                imageDisposable.unsubscribe()
            }

    private fun subscribeToImageCreation(){
        if (creatingImages.compareAndSet(false, true)){
            imageDisposable.unsubscribe()
            imageDisposable = createImages().subscribe({
                creatingImages.compareAndSet(true, false)
            }, Throwable::printStackTrace)
        }
    }

    override fun createImages() : Single<Any>{
        return songGateway.getAllForImageCreation()
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
        FileUtils.makeImages(context, map.value, "folder", map.key.replace(File.separator, ""))
    }

    override fun getAll(): Flowable<List<Folder>> = listObservable

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(folderPath: String): Flowable<List<Song>> {
        var flowable = songMap[folderPath]

        if (flowable == null){
            flowable = songGateway.getAll().map {
                it.asSequence().filter { it.folderPath == folderPath}.toList()
            }.distinctUntilChanged()
                    .replay(1)
                    .refCount()

            songMap[folderPath] = flowable
        }

        return flowable
    }

    override fun getByParam(param: String): Flowable<Folder> {
        return getAll().map { it.first { it.path == param } }
    }

    override fun getMostPlayed(mediaId: MediaId): Flowable<List<Song>> {
        val folderPath = mediaId.categoryValue
        return mostPlayedDao.getAll(folderPath, songGateway.getAll())
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable {
        val songId = mediaId.leaf!!
        return songGateway.getByParam(songId)
                .flatMapCompletable { song ->
                    CompletableSource { mostPlayedDao.insertOne(FolderMostPlayedEntity(0, song.id, song.folderPath)) }
                }
    }
}