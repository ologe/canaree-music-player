package dev.olog.presentation.edit.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.base.viewLifecycleScope
import dev.olog.presentation.databinding.FragmentEditArtistBinding
import dev.olog.presentation.edit.BaseEditItemFragment
import dev.olog.presentation.edit.EditItemViewModel
import dev.olog.presentation.edit.UpdateArtistInfo
import dev.olog.presentation.edit.model.UpdateResult
import dev.olog.shared.android.extensions.afterTextChange
import dev.olog.shared.android.extensions.ctx
import dev.olog.shared.android.extensions.extractText
import dev.olog.shared.android.extensions.getArgument
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.android.viewBinding
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
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

    private val viewModel by viewModels<EditArtistFragmentViewModel>()
    private val editItemViewModel by activityViewModels<EditItemViewModel>()

    private val mediaId by lazyFast {
        MediaId.fromString(getArgument(ARGUMENTS_MEDIA_ID))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.requestData(mediaId)
    }

    private val binding by viewBinding(FragmentEditArtistBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_artist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleScope.launch {
            binding.artist.afterTextChange()
                .map { it.isNotBlank() }
                .collect { binding.okButton.isEnabled = it }
        }

        loadImage(mediaId)

        viewModel.observeData().subscribe(viewLifecycleOwner) {
            binding.artist.setText(it.title)
            binding.albumArtist.setText(it.albumArtist)
            val text = resources.getQuantityString(
                R.plurals.edit_item_xx_tracks_will_be_updated, it.songs, it.songs
            )
            binding.albumsUpdated.text = text
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
        val result = editItemViewModel.updateArtist(
            UpdateArtistInfo(
                mediaId,
                binding.artist.extractText().trim(),
                binding.albumArtist.extractText().trim(),
                binding.podcast.isChecked
            )
        )

        when (result){
            UpdateResult.OK -> dismiss()
            UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_artist_invalid_title)
            else -> {}
        }
    }

    override fun onLoaderCancelled() {
    }
}