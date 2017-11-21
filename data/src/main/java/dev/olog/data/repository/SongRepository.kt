package dev.olog.data.repository

import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC
import android.provider.MediaStore.Audio.Media.DURATION
import android.provider.MediaStore.Audio.Media.TITLE
import com.squareup.sqlbrite2.BriteContentResolver
import dev.olog.data.mapper.toSong
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.SongGateway
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepository @Inject constructor(
        contentResolver: BriteContentResolver

) : SongGateway {

    companion object {

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
                MediaStore.Audio.Media.DATE_ADDED
        )

        private const val SELECTION = "$IS_MUSIC <> ? AND $TITLE NOT LIKE ? AND $DURATION > ?"

        private val SELECTION_ARGS = arrayOf("0", "AUD%", "20000")

        private val SORT_ORDER = null
    }

    private val contentProviderObserver = contentResolver
            .createQuery(
                    MEDIA_STORE_URI,
                    PROJECTION,
                    SELECTION,
                    SELECTION_ARGS,
                    SORT_ORDER,
                    false
            ).mapToList { it.toSong() }
            .toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()
            .replay(1)
            .refCount()

    override fun getAll(): Flowable<List<Song>> = contentProviderObserver

    override fun getByParam(param: Long): Flowable<Song> {
        return getAll().flatMapSingle { it.toFlowable()
                .filter { it.id == param }
                .firstOrError()
        }
    }

    override fun deleteSingle(song: Song): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteGroup(songList: List<Song>): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
