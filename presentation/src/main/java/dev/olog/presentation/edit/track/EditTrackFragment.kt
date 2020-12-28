package dev.olog.presentation.edit.track

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.edit.BaseEditItemFragment
import dev.olog.presentation.edit.EditItemViewModel
import dev.olog.presentation.edit.UpdateSongInfo
import dev.olog.presentation.edit.model.UpdateResult
import dev.olog.shared.android.extensions.*
import kotlinx.android.synthetic.main.fragment_edit_track.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class EditTrackFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditTrackFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: MediaId): EditTrackFragment {
            return EditTrackFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    private val viewModel by viewModels<EditTrackFragmentViewModel>()
    private val editItemViewModel by activityViewModels<EditItemViewModel>()

    private val mediaId by argument(ARGUMENTS_MEDIA_ID, MediaId::fromString)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        launch {
            title.afterTextChange()
                .map { it.isNotBlank() }
                .collect { okButton.isEnabled = it }
        }

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
            }.launchIn(this)
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