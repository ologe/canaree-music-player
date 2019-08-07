package dev.olog.presentation.edit.song

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import dev.olog.core.MediaId
import dev.olog.core.Stylizer
import dev.olog.image.provider.model.OriginalImage
import dev.olog.intents.AppConstants
import dev.olog.presentation.R
import dev.olog.presentation.edit.BaseEditItemFragment
import dev.olog.presentation.edit.EditItemViewModel
import dev.olog.presentation.edit.UpdateSongInfo
import dev.olog.presentation.edit.model.SaveImageType
import dev.olog.presentation.edit.model.UpdateResult
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_edit_track.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import java.lang.Exception
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

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast {
        viewModelProvider<EditTrackFragmentViewModel>(viewModelFactory)
    }
    private val editItemViewModel by lazyFast {
        act.viewModelProvider<EditItemViewModel>(viewModelFactory)
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
            title.afterTextChange()
                .map { it.isNotBlank() }
                .collect { okButton.isEnabled = it }
        }

        loadImage(mediaId)

        viewModel.observeData().subscribe(viewLifecycleOwner) {
            title.setText(it.title)
            artist.setText(it.artist)
            albumArtist.setText(it.albumArtist)
            album.setText(it.album)
            year.setText(it.year)
            genre.setText(it.genre)
            disc.setText(it.disc)
            trackNumber.setText(it.track)
            bitrate.text = it.bitrate
            format.text = it.format
            sampling.text = it.sampling
            podcast.isChecked = it.isPodcast
            hideLoader()
        }
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            launch { trySave() }
        }
        cancelButton.setOnClickListener { dismiss() }
        autoTag.setOnClickListener {
            if (viewModel.fetchSongInfo(mediaId)) {
                showLoader(R.string.edit_song_fetching_info)
            } else {
                ctx.toast(R.string.common_no_internet)
            }
        }
        picker.setOnClickListener { changeImage() }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
        autoTag.setOnClickListener(null)
        picker.setOnClickListener(null)
    }

    private suspend fun trySave() {
        val result = editItemViewModel.updateSong(
            UpdateSongInfo(
                viewModel.getOriginalSong(),
                title.extractText().trim(),
                artist.extractText().trim(),
                albumArtist.extractText().trim(),
                album.extractText().trim(),
                genre.extractText().trim(),
                year.extractText().trim(),
                disc.extractText().trim(),
                trackNumber.extractText().trim(),
                viewModel.getNewImage(),
                podcast.isChecked
            )
        )

        when (result) {
            UpdateResult.OK -> dismiss()
            UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_song_invalid_title)
            UpdateResult.ILLEGAL_DISC_NUMBER -> ctx.toast(R.string.edit_song_invalid_disc_number)
            UpdateResult.ILLEGAL_TRACK_NUMBER -> ctx.toast(R.string.edit_song_invalid_track_number)
            UpdateResult.ILLEGAL_YEAR -> ctx.toast(R.string.edit_song_invalid_year)
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
        viewModel.stopFetch()
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

    override fun provideLayoutId(): Int = R.layout.fragment_edit_track
}