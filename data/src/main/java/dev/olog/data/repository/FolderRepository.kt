package dev.olog.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import dev.olog.data.db.AppDatabase
import dev.olog.data.entity.FolderMostPlayedEntity
import dev.olog.data.utils.FileUtils
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdHelper
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

    private val mostPlayedDao = appDatabase.folderMostPlayedDao()

    private val dataMap : Flowable<MutableMap<String, MutableList<Song>>> = songGateway.getAllNotDistinct()
            .flatMapSingle { it.toFlowable().collectInto(mutableMapOf<String, MutableList<Song>>(), { map, song ->
                if (map.contains(song.folderPath)){
                    map[song.folderPath]!!.add(song)
                } else {
                    map.put(song.folderPath, mutableListOf(song))
                }
            })
            }.replay(1)
            .refCount()

    private val distinctDataMap = dataMap.distinctUntilChanged()

    private val listObservable : Flowable<List<Folder>> = dataMap.flatMapSingle { it.entries.toFlowable()
                .map {
                    val image = FileUtils.folderImagePath(context, it.key)
                    val file = File(image)
                    Folder(it.key.substring(it.key.lastIndexOf(File.separator) + 1),
                            it.key, it.value.size, if (file.exists()) image else "")
                }.toSortedList(compareBy { it.title.toLowerCase() })
            }
            .distinctUntilChanged()
            .replay(1)
            .refCount()

    private val imagesCreated = AtomicBoolean(false)


    override fun getAll(): Flowable<List<Folder>> {
        val compareAndSet = imagesCreated.compareAndSet(false, true)
        if (compareAndSet){
            getAll().firstOrError()
                    .flatMap { it.toFlowable()
                            .flatMapMaybe { folder -> FileUtils.makeImages(context,
                                    observeSongListByParam(folder.path), "folder", folder.path.replace(File.separator, ""))
                                    .subscribeOn(Schedulers.io())
                            }.subscribeOn(Schedulers.io())
                            .buffer(2)
                            .doOnNext { contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null) }
                            .toList()
                    }.subscribe({}, Throwable::printStackTrace)
        }

        return listObservable
    }

    override fun observeSongListByParam(param: String): Flowable<List<Song>> {
        return distinctDataMap.map { it[param]!! }
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