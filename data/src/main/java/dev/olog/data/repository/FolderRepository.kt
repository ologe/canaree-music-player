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
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.flatMapGroup
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase

): FolderGateway {

    companion object {
        private val MEDIA_STORE_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    private val mostPlayedDao = appDatabase.folderMostPlayedDao()

    private val songMap : MutableMap<String, Flowable<List<Song>>> = mutableMapOf()

    private val listObservable = songGateway.getAll()
            .flatMapGroup { distinct { it.folderPath } }
            .flatMapSingle { songsToFolder -> songGateway.getAll().firstOrError()
                    .map { songList ->
                        songsToFolder.map { songToFolder -> songToFolder.toFolder(context,
                                songList.count { it.folderPath == songToFolder.folderPath }) }
                    }
            }.distinctUntilChanged()
            .replay(1)
            .refCount()

    private var imagesCreated = AtomicBoolean(false)

    override fun getAll(): Flowable<List<Folder>> {
        if (imagesCreated.compareAndSet(false, true)){
            songGateway.getAllForImageCreation()
                    .map { it.groupBy { it.folderPath } }
                    .flatMap { it.entries.toFlowable()
                            .parallel()
                            .runOn(Schedulers.io())
                            .map { map -> FileUtils.makeImages(context, map.value, "folder",
                                    map.key.replace(File.separator, "")) }
                            .sequential()
                            .toList()
                            .doOnSuccess { contentResolver.notifyChange(MEDIA_STORE_URI, null) }
                    }.subscribe({}, Throwable::printStackTrace)
        }

        return listObservable
    }

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

    override fun getMostPlayed(param: String): Flowable<List<Song>> {

        return mostPlayedDao.getAll(MediaIdHelper.extractCategoryValue(param), songGateway.getAll())
    }

    override fun insertMostPlayed(mediaId: String): Completable {
        return songGateway.getByParam(MediaIdHelper.extractLeaf(mediaId).toLong())
                .flatMapCompletable { song ->
                    CompletableSource { mostPlayedDao.insertOne(FolderMostPlayedEntity(0, song.id, song.folderPath)) }
                }
    }
}