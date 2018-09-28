package dev.olog.msc.data.repository.podcast

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.database.getLong
import androidx.core.database.getString
import androidx.core.util.getOrDefault
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.data.mapper.toFakePodcast
import dev.olog.msc.data.mapper.toPodcast
import dev.olog.msc.data.mapper.toUneditedPodcast
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.gateway.PodcastGateway
import dev.olog.msc.domain.gateway.UsedImageGateway
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.onlyWithStoragePermission
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.k.extension.debounceFirst
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

private val PROJECTION = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.DATE_ADDED,
        "album_artist"
)

private const val SELECTION = "${MediaStore.Audio.Media.DURATION} > 20000 AND ${MediaStore.Audio.Media.IS_PODCAST} <> 0"

private const val SORT_ORDER = "lower(${MediaStore.Audio.Media.TITLE})"

class PodcastRepository @Inject constructor(
        private val contentResolver: ContentResolver,
        private  val rxContentResolver: BriteContentResolver,
        private  val appPrefsUseCase: AppPreferencesUseCase,
        private  val usedImageGateway: UsedImageGateway

): PodcastGateway {

    private fun queryAllData(): Observable<List<Podcast>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, PROJECTION, SELECTION,
                null, SORT_ORDER, true
        ).onlyWithStoragePermission()
                .debounceFirst()
                .lift(SqlBrite.Query.mapToList { mapToPodcast(it) })
                .map { removeBlacklisted(it) }
                .map { adjustImages(it) }
                .map { mockDataIfNeeded(it) }
                .map { updateImages(it) }
                .onErrorReturn { listOf() }
    }

    private fun mapToPodcast(cursor: Cursor): Podcast {
        return if (AppConstants.useFakeData){
            cursor.toFakePodcast()
        } else {
            cursor.toPodcast()
        }
    }

    private fun updateImages(list: List<Podcast>): List<Podcast>{
        val allForTracks = usedImageGateway.getAllForTracks()
        val allForAlbums = usedImageGateway.getAllForAlbums()
        if (allForTracks.isEmpty() && allForAlbums.isEmpty()){
            return list
        }
        return list.map { podcast ->
            val image = allForTracks.firstOrNull { it.id == podcast.id }?.image // search for track image
                    ?: allForAlbums.firstOrNull { it.id == podcast.albumId }?.image  // search for track album image
                    ?: podcast.image // use default
            podcast.copy(image = image)
        }
    }

    private fun mockDataIfNeeded(original: List<Podcast>): List<Podcast> {
        if (AppConstants.useFakeData && original.isEmpty()){
            return (0 until 50)
                    .map { Podcast(it.toLong(), it.toLong(), it.toLong(),
                            "An awesome title", "An awesome artist",
                            "An awesome album artist", "An awesome album",
                            "", (it * 1000000).toLong(), System.currentTimeMillis(),
                            "storage/emulated/folder", "folder", -1, -1) }
        }
        return original
    }

    private fun adjustImages(original: List<Podcast>): List<Podcast> {
        val images = CommonQuery.searchForImages()
        return original.map { it.copy(image = images.getOrDefault(it.albumId.toInt(), it.image)) }
    }

    private fun removeBlacklisted(original: List<Podcast>): List<Podcast>{
        val blackListed = appPrefsUseCase.getBlackList()
        if (blackListed.isNotEmpty()){
            return original.filter { !blackListed.contains(it.folderPath) }
        }
        return original
    }

    private val cachedData = queryAllData()
            .replay(1)
            .refCount()

    override fun getAll(): Observable<List<Podcast>> = cachedData

    override fun getAllNewRequest(): Observable<List<Podcast>> {
        return queryAllData()
    }

    override fun getByParam(param: Long): Observable<Podcast> {
        return cachedData.map { list -> list.first { it.id == param } }
    }

    @SuppressLint("Recycle")
    override fun getByUri(uri: Uri): Single<Podcast> {
        return Single.fromCallable { getByUriInternal(uri) }
                .map { it.toLong() }
                .flatMap { getByParam(it).firstOrError() }
    }

    @SuppressLint("Recycle")
    private fun getByUriInternal(uri: Uri): String? {
        if (uri.scheme == ContentResolver.SCHEME_CONTENT){
            when (uri.authority){
                "com.android.providers.media.documents" -> return DocumentsContract.getDocumentId(uri).split(":")[1]
                "media" -> return uri.lastPathSegment
            }
        }
        var songFile : File? = null
        if (uri.authority == "com.android.externalstorage.documents"){
            val child = uri.path?.split(":", limit = 2) ?: listOf()
            songFile = File(Environment.getExternalStorageDirectory(), child[1])
        }

        if (songFile == null){
            getFilePathFromUri(uri)?.let { path ->
                songFile = File(path)
            }
        }
        if (songFile == null && uri.path != null){
            songFile = File(uri.path)
        }

        var songId : String? = null

        if (songFile != null){
            contentResolver.query(MEDIA_STORE_URI, arrayOf(BaseColumns._ID),
                    "${ MediaStore.Audio.AudioColumns.DATA} = ?",
                    arrayOf(songFile!!.absolutePath), null)?.let { cursor ->
                cursor.moveToFirst()
                songId = "${cursor.getLong(BaseColumns._ID)}"
                cursor.close()
            }
        }


        return songId
    }

    @SuppressLint("Recycle")
    private fun getFilePathFromUri(uri: Uri): String? {
        var path : String? = null
        contentResolver.query(uri, arrayOf(MediaStore.Audio.Media.DATA),
                null, null, null)?.let { cursor ->
            cursor.moveToFirst()

            path = cursor.getString(MediaStore.Audio.Media.DATA)
            cursor.close()
        }
        return path
    }

    override fun getUneditedByParam(podcastId: Long): Observable<Podcast> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, PROJECTION, "${MediaStore.Audio.Media._ID} = ?",
                arrayOf("$podcastId"), " ${MediaStore.Audio.Media._ID} ASC LIMIT 1", false
        ).onlyWithStoragePermission()
                .debounceFirst()
                .lift(SqlBrite.Query.mapToOne {
                    val id = it.getLong(BaseColumns._ID)
                    val albumId = it.getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)
                    val trackImage = usedImageGateway.getForTrack(id)
                    val albumImage = usedImageGateway.getForAlbum(albumId)
                    val image = trackImage ?: albumImage ?: ImagesFolderUtils.forAlbum(albumId)
                    it.toUneditedPodcast(image)
                }).distinctUntilChanged()
    }

    override fun getAllUnfiltered(): Observable<List<Podcast>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI,
                PROJECTION,
                SELECTION,
                null,
                SORT_ORDER,
                false
        ).onlyWithStoragePermission()
                .debounceFirst()
                .lift(SqlBrite.Query.mapToList { it.toPodcast() })
                .onErrorReturnItem(listOf())
    }

    override fun deleteSingle(podcastId: Long): Completable {
        return Single.fromCallable {
            contentResolver.delete(MEDIA_STORE_URI, "${BaseColumns._ID} = ?", arrayOf("$podcastId"))
        }
                .filter { it > 0 }
                .flatMapSingle { getByParam(podcastId).firstOrError() }
                .map { File(it.path) }
                .filter { it.exists() }
                .map { it.delete() }
                .toSingle()
                .ignoreElement()

    }

    override fun deleteGroup(podcastList: List<Podcast>): Completable {
        return Flowable.fromIterable(podcastList)
                .map { it.id }
                .flatMapCompletable { deleteSingle(it).subscribeOn(Schedulers.io()) }
    }

}