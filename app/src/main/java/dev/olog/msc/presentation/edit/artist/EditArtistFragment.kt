package dev.olog.msc.presentation.edit.artist

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.edit.BaseEditItemFragment
import dev.olog.msc.presentation.edit.EditItemViewModel
import dev.olog.msc.presentation.edit.UpdateArtistInfo
import dev.olog.msc.presentation.edit.UpdateResult
import dev.olog.presentation.model.DisplayableItem
import dev.olog.msc.presentation.utils.lazyFast
import dev.olog.msc.presentation.viewModelProvider
import dev.olog.core.MediaId
import dev.olog.msc.utils.k.extension.*
import kotlinx.android.synthetic.main.fragment_edit_artist.*
import kotlinx.android.synthetic.main.fragment_edit_artist.view.*
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

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<EditArtistFragmentViewModel>(viewModelFactory) }
    private val editItemViewModel by lazyFast { activity!!.viewModelProvider<EditItemViewModel>(viewModelFactory) }

    @Inject lateinit var mediaId: MediaId

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mediaId = MediaId.fromString(getArgument(ARGUMENTS_MEDIA_ID))
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        RxTextView.afterTextChangeEvents(view.artist)
                .map { it.view().text.toString() }
                .map { it.isNotBlank() }
                .asLiveData()
                .subscribe(viewLifecycleOwner, view.okButton::setEnabled)

        viewModel.observeSongList()
                .subscribe(viewLifecycleOwner) {
                    val size = it.size
                    val text = resources.getQuantityString(
                            R.plurals.edit_item_xx_tracks_will_be_updated, size, size)
                    albumsUpdated.text =  text
                }

        viewModel.observeData()
                .subscribe(viewLifecycleOwner) {
                    artist.setText(it.title)
                    albumArtist.setText(it.albumArtist)
                    val model = DisplayableItem(
                        0,
                        MediaId.artistId(it.id),
                        "",
                        image = it.image ?: ""
                    )
                    setImage(model)
                }
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            val result = editItemViewModel.updateArtist(UpdateArtistInfo(
                    mediaId,
                    artist.extractText().trim(),
                    albumArtist.extractText().trim(),
                    viewModel.getNewImage()
            ))

            when (result){
                UpdateResult.OK -> dismiss()
                UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_artist_invalid_title)
                else -> {}
            }
        }
        cancelButton.setOnClickListener { dismiss() }
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

    override fun noImage() {
        viewModel.updateImage(AppConstants.NO_IMAGE)
    }

    override fun onLoaderCancelled() {
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_artist
}