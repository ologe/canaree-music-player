package dev.olog.feature.edit.track

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.edit.*
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.*
import kotlinx.android.synthetic.main.fragment_edit_track.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
internal class EditTrackFragment : BaseEditItemFragment() {

    private val viewModel by viewModels<EditTrackFragmentViewModel>()
    private val editItemViewModel by activityViewModels<EditItemViewModel>()

    private val mediaId by argument<String, MediaId.Track>(Params.MEDIA_ID) {
        MediaId.fromString(it) as MediaId.Track
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        title.afterTextChange()
            .map { it.isNotBlank() }
            .onEach { okButton.isEnabled = it }
            .launchIn(this)

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
                originalSong = viewModel.getOriginalSong(),
                title = title.extractText().trim(),
                artist = artist.extractText().trim(),
                albumArtist = albumArtist.extractText().trim(),
                album = album.extractText().trim(),
                genre = genre.extractText().trim(),
                year = year.extractText().trim(),
                disc = disc.extractText().trim(),
                track = trackNumber.extractText().trim(),
                isPodcast = podcast.isChecked
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