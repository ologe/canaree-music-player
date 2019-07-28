package dev.olog.presentation.edit.artist

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.edit.BaseEditItemFragment
import dev.olog.presentation.edit.EditItemViewModel
import dev.olog.presentation.edit.UpdateArtistInfo
import dev.olog.presentation.edit.model.UpdateResult
import dev.olog.intents.AppConstants
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_edit_artist.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditArtistFragment : BaseEditItemFragment(), CoroutineScope by MainScope() {

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
    private val viewModel by lazyFast {
        viewModelProvider<EditArtistFragmentViewModel>(
            viewModelFactory
        )
    }
    private val editItemViewModel by lazyFast {
        activity!!.viewModelProvider<EditItemViewModel>(
            viewModelFactory
        )
    }

    private val mediaId by lazyFast {
        MediaId.fromString(getArgument(ARGUMENTS_MEDIA_ID))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        launch {
            artist.afterTextChange()
                .map { it.isNotBlank() }
                .collect { okButton.isEnabled = it }
        }

        viewModel.observeSongList()
                .subscribe(viewLifecycleOwner) {
                    val size = it.size
                    val text = resources.getQuantityString(
                            R.plurals.edit_item_xx_tracks_will_be_updated, size, size)
                    albumsUpdated.text =  text
                }

//        viewModel.observeData()
//                .subscribe(viewLifecycleOwner) {
//                    artist.setText(it.title)
//                    albumArtist.setText(it.albumArtist)
//                    setImage(MediaId.artistId(it.id)) TODO
//                }
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            launch {
//                val result = editItemViewModel.updateArtist(
//                    UpdateArtistInfo(
//                        mediaId,
//                        artist.extractText().trim(),
//                        albumArtist.extractText().trim(),
//                        viewModel.getNewImage()
//                    )
//                )
//
//                when (result){
//                    UpdateResult.OK -> dismiss()
//                    UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_artist_invalid_title)
//                    else -> {}
//                }
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