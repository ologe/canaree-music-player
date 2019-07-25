package dev.olog.presentation.edit.song

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import dev.olog.core.MediaId
import dev.olog.core.Stylizer
import dev.olog.presentation.R
import dev.olog.presentation.edit.*
import dev.olog.intents.AppConstants
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_edit_track.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EditTrackFragment : BaseEditItemFragment(), CoroutineScope by MainScope() {

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
        viewModelProvider<EditTrackFragmentViewModel>(
            viewModelFactory
        )
    }
    private val editItemViewModel by lazyFast {
        activity!!.viewModelProvider<EditItemViewModel>(viewModelFactory)
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

        viewModel.observeSong().subscribe(viewLifecycleOwner) {
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
                viewModel.getSong(),
                title.extractText().trim(),
                artist.extractText().trim(),
                albumArtist.extractText().trim(),
                album.extractText().trim(),
                genre.extractText().trim(),
                year.extractText().trim(),
                disc.extractText().trim(),
                trackNumber.extractText().trim(),
                viewModel.getNewImage()
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

    override fun onImagePicked(uri: Uri) {
        viewModel.updateImage(uri.toString())
        loadImage(uri, mediaId)
    }

    override fun restoreImage() {
        launch(Dispatchers.IO) {
            val imageType = viewModel.loadOriginalImage(mediaId)
            withContext(Dispatchers.Main) {
                when (imageType) {
                    is ImageType.String -> loadImage(imageType.url, mediaId)
                    is ImageType.Stream -> loadImage(imageType.stream, mediaId)
                }
            }
        }
    }

    override fun noImage() {
        viewModel.updateImage(AppConstants.NO_IMAGE)
        loadImage(Uri.EMPTY, mediaId)
    }

    override fun onLoaderCancelled() {
        viewModel.stopFetch()
    }

    override fun stylizeImage() {
        launch {
            loadModule()?.let { stylizer ->
                withContext(Dispatchers.IO){
                    val imageType = viewModel.loadOriginalImage(mediaId)
                    val bitmap = when (imageType) {
                        is ImageType.String -> getBitmap(imageType.url)
                        is ImageType.Stream -> getBitmap(imageType.stream)
                    }
                    bitmap?.let { b ->
                        val stylizedBitmap = stylizer.stylize(b)
                        withContext(Dispatchers.Main){
                            loadImage(stylizedBitmap, mediaId)
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadModule(): Stylizer? = suspendCoroutine { continuation ->
        showLoader("Downloading stylize module")

        val splitInstallManager = SplitInstallManagerFactory.create(requireContext())

        val request = SplitInstallRequest.newBuilder()
            .addModule("feature_stylize")
            .build()
//        SplitInstallErrorCode
        splitInstallManager.startInstall(request)
            .addOnSuccessListener { sessionId ->
                hideLoader()
                continuation.resume(Stylizer.loadClass(requireContext()))
            }
            .addOnFailureListener { ex ->
                ex.printStackTrace()
                hideLoader()
                continuation.resume(null)
            }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_track
}