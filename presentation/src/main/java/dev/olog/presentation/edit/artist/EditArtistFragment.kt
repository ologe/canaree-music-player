package dev.olog.presentation.edit.artist

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import dev.olog.core.MediaId
import dev.olog.core.Stylizer
import dev.olog.image.provider.model.OriginalImage
import dev.olog.intents.AppConstants
import dev.olog.presentation.R
import dev.olog.presentation.edit.BaseEditItemFragment
import dev.olog.presentation.edit.EditItemViewModel
import dev.olog.presentation.edit.UpdateArtistInfo
import dev.olog.presentation.edit.model.SaveImageType
import dev.olog.presentation.edit.model.UpdateResult
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_edit_artist.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EditArtistFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditArtistFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): EditArtistFragment {
            return EditArtistFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.requestData(mediaId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        launch {
            artist.afterTextChange()
                .map { it.isNotBlank() }
                .collect { okButton.isEnabled = it }
        }

        loadImage(mediaId)

        viewModel.observeData().subscribe(viewLifecycleOwner) {
            artist.setText(it.title)
            albumArtist.setText(it.albumArtist)
            val text = resources.getQuantityString(
                R.plurals.edit_item_xx_tracks_will_be_updated, it.songs, it.songs
            )
            albumsUpdated.text = text
            podcast.isChecked = it.isPodcast
        }
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            launch { trySave() }
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

    private suspend fun trySave(){
        val result = editItemViewModel.updateArtist(
            UpdateArtistInfo(
                mediaId,
                artist.extractText().trim(),
                albumArtist.extractText().trim(),
                viewModel.getNewImage(),
                podcast.isChecked
            )
        )

        when (result){
            UpdateResult.OK -> dismiss()
            UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_artist_invalid_title)
            else -> {}
        }
    }

    override fun restoreImage() {
        viewModel.restoreOriginalImage()
        loadImage(OriginalImage(mediaId), mediaId)
    }

    override fun noImage() {
        viewModel.updateImage(SaveImageType.Url(AppConstants.NO_IMAGE))
        loadImage(Uri.EMPTY, mediaId)
    }

    override fun onLoaderCancelled() {
    }

    override suspend fun stylizeImage(stylizer: Stylizer) {
        withContext(Dispatchers.IO) {
            try {
                getBitmap(OriginalImage(mediaId), mediaId)
            } catch (ex: Exception){
                withContext(Dispatchers.Main){
                    ctx.toast("Can't stylize default cover")
                }
                ex.printStackTrace()
                return@withContext
            }?.let { bitmap ->
                stylizeImageInternal(stylizer, bitmap)
            }
        }
    }

    private suspend fun stylizeImageInternal(stylizer: Stylizer, bitmap: Bitmap){
        val style = withContext(Dispatchers.Main) {
            Stylizer.loadDialog(act)
        }
        if (style != null){
            withContext(Dispatchers.Main){
                showLoader("Stylizing image", dismissable = false)
            }
            val stylizedBitmap = stylizer.stylize(style, bitmap)
            viewModel.updateImage(SaveImageType.Stylized(stylizedBitmap))
            withContext(Dispatchers.Main) {
                hideLoader()
                loadImage(stylizedBitmap, mediaId)
            }
        }
    }

    override fun toggleDownloadModule(show: Boolean) {
        downloadModule.toggleVisibility(show, true)
    }


    override fun provideLayoutId(): Int = R.layout.fragment_edit_artist
}