package dev.olog.presentation.edit.song

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.base.viewLifecycleScope
import dev.olog.presentation.databinding.FragmentEditTrackBinding
import dev.olog.presentation.edit.BaseEditItemFragment
import dev.olog.presentation.edit.EditItemViewModel
import dev.olog.presentation.edit.UpdateSongInfo
import dev.olog.presentation.edit.model.UpdateResult
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.viewBinding
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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

    private val binding by viewBinding(FragmentEditTrackBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_track, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.requestData(mediaId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleScope.launch {
            binding.title.afterTextChange()
                .map { it.isNotBlank() }
                .collect { binding.okButton.isEnabled = it }
        }

        loadImage(mediaId)

        viewModel.observeData().subscribe(viewLifecycleOwner) {
            binding.title.setText(it.title)
            binding.artist.setText(it.artist)
            binding.albumArtist.setText(it.albumArtist)
            binding.album.setText(it.album)
            binding.year.setText(it.year)
            binding.genre.setText(it.genre)
            binding.disc.setText(it.disc)
            binding.trackNumber.setText(it.track)
            binding.bitrate.text = it.bitrate
            binding.format.text = it.format
            binding.sampling.text = it.sampling
            binding.podcast.isChecked = it.isPodcast
            hideLoader()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.okButton.setOnClickListener {
            viewLifecycleScope.launch { trySave() }
        }
        binding.cancelButton.setOnClickListener { dismiss() }
        binding.autoTag.setOnClickListener {
            if (viewModel.fetchSongInfo(mediaId)) {
                showLoader(R.string.edit_song_fetching_info)
            } else {
                ctx.toast(R.string.common_no_internet)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.okButton.setOnClickListener(null)
        binding.cancelButton.setOnClickListener(null)
        binding.autoTag.setOnClickListener(null)
    }

    private suspend fun trySave() {
        val result = editItemViewModel.updateSong(
            UpdateSongInfo(
                viewModel.getOriginalSong(),
                binding.title.extractText().trim(),
                binding.artist.extractText().trim(),
                binding.albumArtist.extractText().trim(),
                binding.album.extractText().trim(),
                binding.genre.extractText().trim(),
                binding.year.extractText().trim(),
                binding.disc.extractText().trim(),
                binding.trackNumber.extractText().trim(),
                binding.podcast.isChecked
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

    override fun onLoaderCancelled() {
        viewModel.stopFetch()
    }

}