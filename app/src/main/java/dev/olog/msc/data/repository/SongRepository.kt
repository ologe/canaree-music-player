package dev.olog.msc.data.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media.DURATION
import androidx.core.util.getOrDefault
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.data.mapper.toFakeSong
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.data.mapper.toUneditedSong
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.domain.gateway.UsedImageGateway
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.onlyWithStoragePermission
import dev.olog.msc.utils.getLong
import dev.olog.msc.utils.getString
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
        MediaStore.Audio.Media.IS_PODCAST,
        "album_artist"
)

private const val SELECTION = "$DURATION > 20000 AND ${MediaStore.Audio.Media.IS_PODCAST} = 0"

private const val SORT_ORDER = "lower(${MediaStore.Audio.Media.TITLE})"

class SongRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private  val rxContentResolver: BriteContentResolver,
        private  val appPrefsUseCase: AppPreferencesGateway,
        private  val usedImageGateway: UsedImageGateway

) : SongGateway {

    private fun queryAllData(): Observable<List<Song>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, PROJECTION, SELECTION,
                null, SORT_ORDER, true
        ).onlyWithStoragePermission()
                .debounceFirst()
                .lift(SqlBrite.Query.mapToList { mapToSong(it) })
                .map { removeBlacklisted(it) }
                .map { adjustImages(it) }
                .map { mockDataIfNeeded(it) }
                .map { updateImages(it) }
                .doOnError { it.printStackTrace() }
                .onErrorReturn { listOf() }
    }

    private fun mapToSong(cursor: Cursor): Song {
        return if (AppConstants.useFakeData){
            cursor.toFakeSong()
        } else {
            cursor.toSong()
        }
    }

    private fun updateImages(list: List<Song>): List<Song>{
        if (AppConstants.useFakeData){
            return list
        }

        val allForTracks = usedImageGateway.getAllForTracks()
        val allForAlbums = usedImageGateway.getAllForAlbums()
        if (allForTracks.isEmpty() && allForAlbums.isEmpty()){
            return list
        }
        return list.map { song ->
            val image = allForTracks.firstOrNull { it.id == song.id }?.image // search for track image
                    ?: allForAlbums.firstOrNull { it.id == song.albumId }?.image  // search for track album image
                    ?: song.image // use default
            song.copy(image = image)
        }
    }

    private fun mockDataIfNeeded(original: List<Song>): List<Song> {
        if (AppConstants.useFakeData && original.isEmpty()){
            return (0 until 50)
                    .map { Song(it.toLong(), it.toLong(), it.toLong(),
                            "An awesome title", "An awesome artist",
                            "An awesome album artist", "An awesome album",
                            "", (it * 1000000).toLong(), System.currentTimeMillis(),
                            "storage/emulated/folder", "folder", -1, -1) }
        }
        return original
    }

    private fun adjustImages(original: List<Song>): List<Song> {
        if (AppConstants.useFakeData){
            return original.map { it.copy(image = ImagesFolderUtils.getAssetImage(it.albumId, it.id)) }
        }
        val images = CommonQuery.searchForImages(context)
        return original.map { it.copy(image = images.getOrDefault(it.albumId.toInt(), it.image)) }
    }

    private fun removeBlacklisted(original: List<Song>): List<Song>{
        val blackListed = appPrefsUseCase.getBlackList()
        if (blackListed.isNotEmpty()){
            return original.filter { !blackListed.contains(it.folderPath) }
        }
        return original
    }

    private val cachedData = queryAllData()
            .replay(1)
            .refCount()

    override fun getAll(): Observable<List<Song>> = cachedData

    override fun getAllNewRequest(): Observable<List<Song>> {
        return queryAllData()
    }

    override fun getByParam(param: Long): Observable<Song> {
        return cachedData.map { list -> list.first { it.id == param } }
    }

    override fun getByAlbumId(albumId: Long): Observable<Song> {
        return cachedData.map { list -> list.first { it.albumId == albumId } }
    }

    @SuppressLint("Recycle")
    override fun getByUri(uri: Uri): Single<Song> {
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
            context.contentResolver.query(MEDIA_STORE_URI, arrayOf(BaseColumns._ID),
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
        context.contentResolver.query(uri, arrayOf(MediaStore.Audio.Media.DATA),
                null, null, null)?.let { cursor ->
            cursor.moveToFirst()

            path = cursor.getString(MediaStore.Audio.Media.DATA)
            cursor.close()
        }
        return path
    }

    override fun getUneditedByParam(songId: Long): Observable<Song> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, PROJECTION, "${MediaStore.Audio.Media._ID} = ?",
                arrayOf("$songId"), " ${MediaStore.Audio.Media._ID} ASC LIMIT 1", false
        ).onlyWithStoragePermission()
                .debounceFirst()
                .lift(SqlBrite.Query.mapToOne {
                    val id = it.getLong(BaseColumns._ID)
                    val albumId = it.getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)
                    val trackImage = usedImageGateway.getForTrack(id)
                    val albumImage = usedImageGateway.getForAlbum(albumId)
                    val image = trackImage ?: albumImage ?: ImagesFolderUtils.forAlbum(albumId)
                    it.toUneditedSong(image)
        }).distinctUntilChanged()
    }

    override fun getAllUnfiltered(): Observable<List<Song>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI,
                PROJECTION,
                SELECTION,
                null,
                SORT_ORDER,
                false
        ).onlyWithStoragePermission()
                .debounceFirst()
                .lift(SqlBrite.Query.mapToList { it.toSong() })
                .doOnError { it.printStackTrace() }
                .onErrorReturnItem(listOf())
    }

    override fun deleteSingle(songId: Long): Completable {
        return Single.fromCallable {
            context.contentResolver.delete(MEDIA_STORE_URI, "${BaseColumns._ID} = ?", arrayOf("$songId"))
        }
                .filter { it > 0 }
                .flatMapSingle { getByParam(songId).firstOrError() }
                .map { File(it.path) }
                .filter { it.exists() }
                .map { it.delete() }
                .toSingle()
                .ignoreElement()

    }

    override fun deleteGroup(songList: List<Song>): Completable {
        return Flowable.fromIterable(songList)
                .map { it.id }
                .flatMapCompletable { deleteSingle(it).subscribeOn(Schedulers.io()) }
    }

}

