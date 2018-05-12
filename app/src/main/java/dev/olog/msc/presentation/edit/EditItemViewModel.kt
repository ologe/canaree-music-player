package dev.olog.msc.presentation.edit

import android.arch.lifecycle.ViewModel
import androidx.core.text.isDigitsOnly
import dev.olog.msc.R
import dev.olog.msc.app.app
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import java.io.FileNotFoundException

class EditItemViewModel(
        private val presenter: EditItemPresenter

) : ViewModel() {

    private val subscriptions = CompositeDisposable()

    fun updateSong(data: UpdateSongInfo): UpdateResult {
        when {
            data.title.isBlank() -> return UpdateResult.EMPTY_TITLE
            data.year.isNotBlank() && !data.year.isDigitsOnly() -> return UpdateResult.ILLEGAL_YEAR
            data.disc.isNotBlank() && !data.disc.isDigitsOnly() -> return UpdateResult.ILLEGAL_DISC_NUMBER
            data.track.isNotBlank() && !data.track.isDigitsOnly() -> return UpdateResult.ILLEGAL_TRACK_NUMBER
        }

        presenter.deleteTrack(data.originalSong.id)
                .andThen(presenter.updateSingle(data))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ app.toast(R.string.edit_track_update_success) }, { showErrorMessage(it) })
                .addTo(subscriptions)

        return UpdateResult.OK
    }

    fun updateAlbum(data: UpdateAlbumInfo): UpdateResult {
        when {
            data.title.isBlank() -> return UpdateResult.EMPTY_TITLE
            data.year.isNotBlank() && !data.year.isDigitsOnly() -> return UpdateResult.ILLEGAL_YEAR
        }

        presenter.deleteAlbum(data.mediaId.resolveId)
                .andThen(presenter.updateAlbum(data))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ app.toast(R.string.edit_album_update_success) }, { showErrorMessage(it) })
                .addTo(subscriptions)

        return UpdateResult.OK
    }

    fun updateArtist(data: UpdateArtistInfo): UpdateResult {
        when {
            data.name.isBlank() -> return UpdateResult.EMPTY_TITLE
        }

        presenter.deleteArtist(data.mediaId.resolveId)
                .andThen(presenter.updateArtist(data))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ app.toast(R.string.edit_artist_update_success) }, { showErrorMessage(it) })
                .addTo(subscriptions)

        return UpdateResult.OK
    }

    private fun showErrorMessage(throwable: Throwable){
        when (throwable){
            is CannotReadException -> app.toast(R.string.edit_song_cannot_read)
            is ReadOnlyFileException -> app.toast(R.string.edit_song_read_only)
            is FileNotFoundException -> app.toast(R.string.edit_song_file_not_found)
            else -> app.toast(R.string.popup_error_message)
        }
    }

    override fun onCleared() {
        subscriptions.clear()
    }

}

data class UpdateSongInfo(
        val originalSong: Song,
        val title: String,
        val artist: String,
        val album: String,
        val genre: String,
        val year: String,
        val disc: String,
        val track: String,
        val image: String?
)

data class UpdateAlbumInfo(
        val mediaId: MediaId,
        val title: String,
        val artist: String,
        val genre: String,
        val year: String,
        val image: String?
)

data class UpdateArtistInfo(
        val mediaId: MediaId,
        val name: String,
        val image: String?
)