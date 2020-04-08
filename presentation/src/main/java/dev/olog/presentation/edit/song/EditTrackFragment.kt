package dev.olog.presentation.edit.song

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.olog.feature.presentation.base.extensions.onClick
import dev.olog.feature.presentation.base.extensions.toast
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.presentation.R
import dev.olog.presentation.edit.BaseEditItemFragment
import dev.olog.presentation.edit.EditItemViewModel
import dev.olog.presentation.edit.UpdateSongInfo
import dev.olog.presentation.edit.model.UpdateResult
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_edit_track.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class EditTrackFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditTrackFragment"
        const val ARGUMENTS_MEDIA_ID = "${TAG}_arguments_media_id"

        @JvmStatic
        fun newInstance(mediaId: PresentationId.Track): EditTrackFragment {
            return EditTrackFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId
            )
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<EditTrackFragmentViewModel> {
        viewModelFactory
    }
    private val editItemViewModel by activityViewModels<EditItemViewModel> {
        viewModelFactory
    }

    private val mediaId by lazyFast {
        getArgument<PresentationId.Track>(ARGUMENTS_MEDIA_ID)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.requestData(mediaId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        title.afterTextChange()
            .map { it.isNotBlank() }
            .onEach { okButton.isEnabled = it }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        loadImage(mediaId)

        viewModel.observeData()
            .onEach {
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
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
        okButton.onClick { trySave() }
        cancelButton.setOnClickListener { dismiss() }
        autoTag.setOnClickListener {
            if (viewModel.fetchSongInfo(mediaId)) {
                showLoader(R.string.edit_song_fetching_info)
            } else {
                requireContext().toast(R.string.common_no_internet)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
        autoTag.setOnClickListener(null)
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
                podcast.isChecked
            )
        )

        when (result) {
            UpdateResult.OK -> dismiss()
            UpdateResult.EMPTY_TITLE -> requireContext().toast(R.string.edit_song_invalid_title)
            UpdateResult.ILLEGAL_DISC_NUMBER -> requireContext().toast(R.string.edit_song_invalid_disc_number)
            UpdateResult.ILLEGAL_TRACK_NUMBER -> requireContext().toast(R.string.edit_song_invalid_track_number)
            UpdateResult.ILLEGAL_YEAR -> requireContext().toast(R.string.edit_song_invalid_year)
        }
    }

    override fun onLoaderCancelled() {
        viewModel.stopFetch()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_track
}