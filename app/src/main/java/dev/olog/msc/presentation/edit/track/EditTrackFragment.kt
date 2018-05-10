package dev.olog.msc.presentation.edit.track

import android.os.Bundle
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.presentation.edit.BaseEditItemFragment
import dev.olog.msc.presentation.edit.UpdateResult
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.*
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import java.io.IOException
import javax.inject.Inject

class EditTrackFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditTrackFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): EditTrackFragment {
            return EditTrackFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject lateinit var viewModel: EditTrackFragmentViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        RxTextView.afterTextChangeEvents(title)
                .map { it.view().text.toString() }
                .map { it.isNotBlank() }
                .asLiveData()
                .subscribe(this, okButton::setEnabled)

        viewModel.observeData().subscribe(this, {
            title.setText(it.title)
            artist.setText(it.artist)
            album.setText(it.album)
            year.setText(it.year)
            genre.setText(it.genre)
            disc.setText(it.disc)
            trackNumber.setText(it.track)
            val model = DisplayableItem(0, MediaId.songId(it.id), "", it.image)
            setImage(model)
            hideLoader()
        })

        viewModel.observeTaggerErrors()
                .subscribe(this, {
                    when (it){
                        is CannotReadException -> ctx.toast(R.string.edit_song_error_can_not_read)
                        is IOException -> ctx.toast(R.string.edit_song_error_io)
                        is ReadOnlyFileException -> ctx.toast(R.string.edit_song_error_read_only)
                        else -> {
                            // TagException, InvalidAudioFrameException
                            ctx.toast(R.string.edit_song_error)
                        }
                    }
                    act.onBackPressed()
                })
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            val result = viewModel.updateMetadata(
                    title.extractText(),
                    artist.extractText(),
                    album.extractText(),
                    genre.extractText(),
                    year.extractText().trim(),
                    disc.extractText().trim(),
                    trackNumber.extractText().trim())

            when (result){
                UpdateResult.OK -> {
//                    ctx.toast(R.string.edit_track_update_success)
                    act.onBackPressed()
                }
                UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_song_invalid_title)
                UpdateResult.ILLEGAL_DISC_NUMBER -> ctx.toast(R.string.edit_song_invalid_disc_number)
                UpdateResult.ILLEGAL_TRACK_NUMBER -> ctx.toast(R.string.edit_song_invalid_track_number)
                UpdateResult.ILLEGAL_YEAR -> ctx.toast(R.string.edit_song_invalid_year)
                UpdateResult.ERROR -> ctx.toast(R.string.popup_error_message)
                UpdateResult.CANNOT_READ -> ctx.toast(R.string.edit_song_cannot_read)
                UpdateResult.READ_ONLY -> ctx.toast(R.string.edit_song_read_only)
                UpdateResult.FILE_NOT_FOUND -> ctx.toast(R.string.edit_song_file_not_found)
            }
        }
        cancelButton.setOnClickListener { act.onBackPressed() }
        autoTag.setOnClickListener {
            viewModel.fetchSongInfo()
            showLoader(R.string.edit_song_fetching_info)
        }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
        autoTag.setOnClickListener(null)
    }

    override fun onLoaderCancelled() {
        viewModel.stopFetching()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_track
}