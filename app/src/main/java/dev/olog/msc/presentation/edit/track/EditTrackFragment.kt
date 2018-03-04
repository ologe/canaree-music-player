package dev.olog.msc.presentation.edit.track

import android.arch.lifecycle.Observer
import android.net.Uri
import android.os.Bundle
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.presentation.edit.BaseEditItemFragment
import dev.olog.msc.presentation.edit.UpdateResult
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.*
import kotlinx.android.synthetic.main.fragment_edit_track.*
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

        viewModel.observeData().observe(this, Observer {
                    when (it){
                        null -> ctx.toast(R.string.edit_song_info_not_found)
                        else -> {
                            title.setText(it.title)
                            artist.setText(it.artist)
                            album.setText(it.album)
                            year.setText(it.year)
                            genre.setText(it.genre)
                            disc.setText(it.disc)
                            trackNumber.setText(it.track)
                        }
                    }
                    hideLoader()
                })

        viewModel.observeImage().observe(this, Observer {
                    when (it){
                        null -> ctx.toast(R.string.edit_song_image_not_found)
                        else -> setImage(it, viewModel.getSongId())
                    }
                    hideLoader()
                })

        viewModel.observeConnectivity()
                .asLiveData()
                .subscribe(this, { ctx.toast(it) })
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
                UpdateResult.OK -> act.onBackPressed()
                UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_song_invalid_title)
                UpdateResult.ILLEGAL_DISC_NUMBER -> ctx.toast(R.string.edit_song_invalid_disc_number)
                UpdateResult.ILLEGAL_TRACK_NUMBER -> ctx.toast(R.string.edit_song_invalid_track_number)
                UpdateResult.ILLEGAL_YEAR -> ctx.toast(R.string.edit_info_year)
                UpdateResult.ERROR -> ctx.toast(R.string.popup_error_message)
            }
        }
        cancelButton.setOnClickListener { act.onBackPressed() }
        autoTag.setOnClickListener {
            viewModel.fetchSongInfo()
            showLoader(R.string.edit_song_fetching_info)
        }
        changeAlbumArt.setOnClickListener {
            val items = arrayOf(
                    ctx.getString(R.string.edit_item_image_fetch),
                    ctx.getString(R.string.edit_item_image_choose_local),
                    ctx.getString(R.string.edit_item_image_restore)
            )
            showImageChooser(items, { _, which ->
                when (which){
                    0 -> {
                        viewModel.fetchAlbumArt()
                        showLoader(R.string.edit_song_fetching_image)
                    }
                    1 -> loadLocalImage()
                    2 -> viewModel.restoreAlbumArt()
                }
            })
        }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
        autoTag.setOnClickListener(null)
        changeAlbumArt.setOnClickListener(null)
    }

    override fun onLoaderCancelled() {
        viewModel.stopFetching()
    }

    override fun onLocalImageLoaded(uri: Uri) {
        viewModel.setAlbumArt(uri)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_track
}