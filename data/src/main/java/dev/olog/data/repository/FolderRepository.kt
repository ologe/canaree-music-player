package dev.olog.data.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import dev.olog.data.db.AppDatabase
import dev.olog.data.entity.FolderMostPlayedEntity
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
import java.io.File
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

    private val dataMap : Flowable<MutableMap<String, MutableList<Song>>> = songGateway.getAll()
            .flatMapSingle { it.toFlowable().collectInto(mutableMapOf<String, MutableList<Song>>(), { map, song ->
                if (map.contains(song.folderPath)){
                    map[song.folderPath]!!.add(song)
                } else {
                    map.put(song.folderPath, mutableListOf(song))
                }
            })
            }
            .distinctUntilChanged()
            .replay(1)
            .refCount()

    private val listObservable : Flowable<List<Folder>> = dataMap.flatMapSingle { it.entries.toFlowable()
                .map {
                    val dataDir = "${context.applicationInfo.dataDir}${File.separator}folder${File.separator}"
                    val image = "$dataDir${it.key.replace(File.separator, "")}"
                    Folder(it.key.substring(it.key.lastIndexOf(File.separator) + 1),
                            it.key, it.value.size, image)
                }.toSortedList(compareBy { it.title.toLowerCase() })
            }
            .distinctUntilChanged()
            .replay(1)
            .refCount()



    private var imagesCreated = false

//    private val images = dataMap
//            .firstOrError()
//            .flatMap { it.entries.toFlowable()
//            .map { it.value }
//            .flatMapSingle { songList -> songList.toFlowable()
//                    .map { it.albumId }
//                    .map { ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), it) }
//                    .map { uri -> try {
//                        Option(MediaStore.Images.Media.getBitmap(contentResolver, uri))
//                    } catch (ex: Exception){
//                        Option(null)
//                    }}
//                    .filter { it.item != null }
//                    .map { it.item!! }
//                    .take(4)
//                    .toList()
//                    .map { ImageUtils.joinImages(it) }
//                    .map {
//                        val song = songList[0]
//                        val name = song.folderPath.replace(File.separator, "", false)
//                        FileUtils.saveFile(context, "folder",name, it)
//                    }
//                    .subscribeOn(Schedulers.io())
//            }.toList()
//    }.subscribeOn(Schedulers.io())
//            .toCompletable()

    @SuppressLint("CheckResult")
    override fun getAll(): Flowable<List<Folder>> {

//        if (!imagesCreated){
//            imagesCreated = true
//            images.subscribe({
//                contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
//
//            }, Throwable::printStackTrace)
//        }

        return listObservable
    }

    override fun observeSongListByParam(param: String): Flowable<List<Song>> {
        return dataMap.map { it[param]!! }
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