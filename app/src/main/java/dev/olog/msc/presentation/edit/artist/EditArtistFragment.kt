package dev.olog.msc.presentation.edit.artist

import android.arch.lifecycle.Observer
import android.net.Uri
import android.os.Bundle
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.presentation.edit.BaseEditItemFragment
import dev.olog.msc.presentation.edit.UpdateResult
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.*
import kotlinx.android.synthetic.main.fragment_edit_artist.*
import javax.inject.Inject

class EditArtistFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditArtistFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): EditArtistFragment {
            return EditArtistFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString())
        }
    }

    @Inject lateinit var viewModel: EditArtistFragmentViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        RxTextView.afterTextChangeEvents(artist)
                .map { it.view().text.toString() }
                .map { it.isNotBlank() }
                .asLiveData()
                .subscribe(this, okButton::setEnabled)

        viewModel.observeSongList()
                .subscribe(this, {
                    val size = it.size
                    val text = resources.getQuantityString(
                            R.plurals.edit_item_xx_tracks_will_be_updated, size, size)
                    albumsUpdated.text =  text
                })

        viewModel.observeImage().observe(this, Observer {
            when (it){
                null -> ctx.toast(R.string.edit_song_image_not_found)
                else -> setImage(it, viewModel.getArtistId())
            }
            hideLoader()
        })
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            val result = viewModel.updateMetadata(artist.extractText())

            when (result){
                UpdateResult.OK -> act.onBackPressed()
                UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_artist_invalid_title)
                UpdateResult.ERROR -> ctx.toast(R.string.popup_error_message)
                else -> throw IllegalArgumentException("invalid result $result")
            }
        }
        cancelButton.setOnClickListener { act.onBackPressed() }
        changeAlbumArt.setOnClickListener {
            val items = arrayOf(
                    ctx.getString(R.string.edit_item_image_choose_local),
                    ctx.getString(R.string.edit_item_image_restore)
            )
            showImageChooser(items, { _, which ->
                when (which){
                    0 -> loadLocalImage()
                    1 -> viewModel.restoreAlbumArt()
                }
            })
        }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
        changeAlbumArt.setOnClickListener(null)
    }

    override fun onLoaderCancelled() {

    }

    override fun onLocalImageLoaded(uri: Uri) {
        viewModel.setAlbumArt(uri.toString())
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_artist
}