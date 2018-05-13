package dev.olog.msc.presentation.edit.artist

import android.net.Uri
import android.os.Bundle
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.presentation.edit.BaseEditItemFragment
import dev.olog.msc.presentation.edit.EditItemViewModel
import dev.olog.msc.presentation.edit.UpdateArtistInfo
import dev.olog.msc.presentation.edit.UpdateResult
import dev.olog.msc.presentation.model.DisplayableItem
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
    @Inject lateinit var editItemViewModel: EditItemViewModel
    @Inject lateinit var mediaId: MediaId

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

        viewModel.observeData()
                .subscribe(this, {
                    artist.setText(it.title)
                    val model = DisplayableItem(0, MediaId.artistId(it.id), "", image = it.image ?: "")
                    setImage(model)
                })
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            val result = editItemViewModel.updateArtist(UpdateArtistInfo(
                    mediaId,
                    artist.extractText(),
                    viewModel.getNewImage()
            ))

            when (result){
                UpdateResult.OK -> act.onBackPressed()
                UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_artist_invalid_title)
                else -> {}
            }
        }
        cancelButton.setOnClickListener { act.onBackPressed() }
        picker.setOnClickListener { changeImage() }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
        picker.setOnClickListener(null)
    }

    override fun restoreImage() {
        viewModel.updateImage(null)
    }

    override fun onImagePicked(uri: Uri) {
        viewModel.updateImage(uri.toString())
    }

    override fun onLoaderCancelled() {
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_artist
}