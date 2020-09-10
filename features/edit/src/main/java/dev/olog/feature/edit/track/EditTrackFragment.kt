package dev.olog.feature.edit.track

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.MediaId
import dev.olog.feature.presentation.base.extensions.*
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.toPresentation
import dev.olog.feature.edit.BaseEditItemFragment
import dev.olog.feature.edit.EditItemViewModel
import dev.olog.feature.edit.R
import dev.olog.feature.edit.model.UpdateSongInfo
import dev.olog.lib.audio.tagger.model.Tags
import dev.olog.feature.edit.model.UpdateResult
import dev.olog.shared.android.extensions.extractText
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class EditTrackFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditTrackFragment"
        const val ARGUMENTS_MEDIA_ID = "${TAG}_arguments_media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId.Track): EditTrackFragment {
            return EditTrackFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId.toPresentation()
            )
        }
    }

    private val viewModel by viewModels<EditTrackFragmentViewModel>()
    private val editItemViewModel by activityViewModels<EditItemViewModel>()

    private val mediaId by lazyFast {
        getArgument<PresentationId.Track>(ARGUMENTS_MEDIA_ID)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.requestData(mediaId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        title.afterTextChange()
//            .map { it.isNotBlank() }
//            .onEach { okButton.isEnabled = it }
//            .launchIn(viewLifecycleOwner.lifecycleScope)

        loadImage(mediaId)

        viewModel.observeData()
            .onEach {
//                title.setText(it.title)
//                artist.setText(it.artist)
//                albumArtist.setText(it.albumArtist)
//                album.setText(it.album)
//                year.setText(it.year)
//                genre.setText(it.genre)
//                disc.setText(it.disc)
//                trackNumber.setText(it.track)
//                bitrate.text = it.bitrate
//                format.text = it.format
//                sampling.text = it.sampling
//                podcast.isChecked = it.isPodcast
                hideLoader()
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
//        okButton.onClick { trySave() }
//        cancelButton.setOnClickListener { dismiss() }
//        autoTag.setOnClickListener {
//            if (viewModel.fetchSongInfo(mediaId)) {
//                showLoader(R.string.edit_song_fetching_info)
//            } else {
//                requireContext().toast(R.string.common_no_internet)
//            }
//        }
    }

    override fun onPause() {
        super.onPause()
//        okButton.setOnClickListener(null)
//        cancelButton.setOnClickListener(null)
//        autoTag.setOnClickListener(null)
    }

    private suspend fun trySave() {
//        val result = editItemViewModel.updateSong(
//            UpdateSongInfo(
//                trackId = viewModel.getOriginalSong().id,
//                path = viewModel.getOriginalSong().path,
//                isPodcast = podcast.isChecked,
//                tags = Tags(
//                    title = title.extractText().trim(),
//                    artist = artist.extractText().trim(),
//                    albumArtist = albumArtist.extractText().trim(),
//                    album = album.extractText().trim(),
//                    genre = genre.extractText().trim(),
//                    year = year.extractText().trim(),
//                    discNo = disc.extractText().trim(),
//                    trackNo = trackNumber.extractText().trim()
//                )
//            )
//        )
//
//        when (result) {
//            UpdateResult.OK -> dismiss()
//            UpdateResult.EMPTY_TITLE -> requireContext().toast(R.string.edit_song_invalid_title)
//            UpdateResult.ILLEGAL_DISC_NUMBER -> requireContext().toast(R.string.edit_song_invalid_disc_number)
//            UpdateResult.ILLEGAL_TRACK_NUMBER -> requireContext().toast(R.string.edit_song_invalid_track_number)
//            UpdateResult.ILLEGAL_YEAR -> requireContext().toast(R.string.edit_song_invalid_year)
//        }
    }

    override fun onLoaderCancelled() {
        viewModel.stopFetch()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_track
}