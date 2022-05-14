package dev.olog.feature.edit.song

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.feature.edit.BaseEditItemFragment
import dev.olog.feature.edit.EditItemViewModel
import dev.olog.feature.edit.R
import dev.olog.feature.edit.UpdateResult
import dev.olog.feature.edit.UpdateSongInfo
import dev.olog.shared.extension.afterTextChange
import dev.olog.shared.extension.argument
import dev.olog.shared.extension.collectOnViewLifecycle
import dev.olog.shared.extension.extractText
import dev.olog.shared.extension.launchWhenResumed
import dev.olog.shared.extension.subscribe
import dev.olog.shared.extension.toast
import dev.olog.shared.extension.withArguments
import kotlinx.android.synthetic.main.fragment_edit_track.*
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class EditTrackFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditTrackFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): EditTrackFragment {
            return EditTrackFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId
            )
        }
    }

    private val viewModel by viewModels<EditTrackFragmentViewModel>()
    private val editItemViewModel by activityViewModels<EditItemViewModel>()

    private val mediaId by argument<MediaId>(ARGUMENTS_MEDIA_ID)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.requestData(mediaId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        title.afterTextChange()
            .map { it.isNotBlank() }
            .collectOnViewLifecycle(this) { okButton.isEnabled = it }

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
            launchWhenResumed { trySave() }
        }
        cancelButton.setOnClickListener { dismiss() }
        autoTag.setOnClickListener {
            if (viewModel.fetchSongInfo(mediaId)) {
                showLoader(localization.R.string.edit_song_fetching_info)
            } else {
                toast(localization.R.string.common_no_internet)
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
            UpdateResult.EMPTY_TITLE -> toast(localization.R.string.edit_song_invalid_title)
            UpdateResult.ILLEGAL_DISC_NUMBER -> toast(localization.R.string.edit_song_invalid_disc_number)
            UpdateResult.ILLEGAL_TRACK_NUMBER -> toast(localization.R.string.edit_song_invalid_track_number)
            UpdateResult.ILLEGAL_YEAR -> toast(localization.R.string.edit_song_invalid_year)
        }
    }

    override fun onLoaderCancelled() {
        viewModel.stopFetch()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_track
}