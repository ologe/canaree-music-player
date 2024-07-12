package dev.olog.presentation.edit.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.base.viewLifecycleScope
import dev.olog.presentation.databinding.FragmentEditAlbumBinding
import dev.olog.presentation.edit.BaseEditItemFragment
import dev.olog.presentation.edit.EditItemViewModel
import dev.olog.presentation.edit.UpdateAlbumInfo
import dev.olog.presentation.edit.model.UpdateResult
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.viewBinding
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditAlbumFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditAlbumFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): EditAlbumFragment {
            return EditAlbumFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString())
        }
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast {
        viewModelProvider<EditAlbumFragmentViewModel>(viewModelFactory)
    }
    private val editItemViewModel by lazyFast {
        act.viewModelProvider<EditItemViewModel>(viewModelFactory)
    }
    private val mediaId: MediaId by lazyFast {
        MediaId.fromString(getArgument(ARGUMENTS_MEDIA_ID))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.requestData(mediaId)
    }

    private val binding by viewBinding(FragmentEditAlbumBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleScope.launch {
            binding.album.afterTextChange()
                .map { it.isNotBlank() }
                .collect { binding.okButton.isEnabled = it }
        }

        loadImage(mediaId)

        viewModel.observeData().subscribe(viewLifecycleOwner) {
            binding.album.setText(it.title)
            binding.artist.setText(it.artist)
            binding.albumArtist.setText(it.albumArtist)
            binding.year.setText(it.year)
            binding.genre.setText(it.genre)
            val text = resources.getQuantityString(
                R.plurals.edit_item_xx_tracks_will_be_updated, it.songs, it.songs)
            binding.albumsUpdated.text =  text
            binding.podcast.isChecked = it.isPodcast
        }
    }

    override fun onResume() {
        super.onResume()
        binding.okButton.setOnClickListener {
            viewLifecycleScope.launch { trySave() }
        }
        binding.cancelButton.setOnClickListener { dismiss() }
    }

    override fun onPause() {
        super.onPause()
        binding.okButton.setOnClickListener(null)
        binding.cancelButton.setOnClickListener(null)
    }

    private suspend fun trySave(){
        val result = editItemViewModel.updateAlbum(
            UpdateAlbumInfo(
                mediaId,
                binding.album.extractText().trim(),
                binding.artist.extractText().trim(),
                binding.albumArtist.extractText().trim(),
                binding.genre.extractText().trim(),
                binding.year.extractText().trim(),
                binding.podcast.isChecked
            )
        )

        when (result){
            UpdateResult.OK -> dismiss()
            UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_song_invalid_title)
            UpdateResult.ILLEGAL_YEAR -> ctx.toast(R.string.edit_song_invalid_year)
            UpdateResult.ILLEGAL_DISC_NUMBER,
            UpdateResult.ILLEGAL_TRACK_NUMBER -> {}
        }
    }

    override fun onLoaderCancelled() {
    }

}