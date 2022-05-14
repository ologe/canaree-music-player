package dev.olog.feature.edit.artist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.feature.edit.BaseEditItemFragment
import dev.olog.feature.edit.EditItemViewModel
import dev.olog.feature.edit.R
import dev.olog.feature.edit.UpdateArtistInfo
import dev.olog.feature.edit.UpdateResult
import dev.olog.shared.extension.afterTextChange
import dev.olog.shared.extension.argument
import dev.olog.shared.extension.collectOnViewLifecycle
import dev.olog.shared.extension.extractText
import dev.olog.shared.extension.launchWhenResumed
import dev.olog.shared.extension.subscribe
import dev.olog.shared.extension.toast
import dev.olog.shared.extension.withArguments
import kotlinx.android.synthetic.main.fragment_edit_artist.*
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class EditArtistFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditArtistFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): EditArtistFragment {
            return EditArtistFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId
            )
        }
    }

    private val viewModel by viewModels<EditArtistFragmentViewModel>()
    private val editItemViewModel by activityViewModels<EditItemViewModel>()

    private val mediaId by argument<MediaId>(ARGUMENTS_MEDIA_ID)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.requestData(mediaId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        artist.afterTextChange()
            .map { it.isNotBlank() }
            .collectOnViewLifecycle(this) { okButton.isEnabled = it }

        loadImage(mediaId)

        viewModel.observeData().subscribe(viewLifecycleOwner) {
            artist.setText(it.title)
            albumArtist.setText(it.albumArtist)
            val text = resources.getQuantityString(
                localization.R.plurals.edit_item_xx_tracks_will_be_updated, it.songs, it.songs
            )
            albumsUpdated.text = text
            podcast.isChecked = it.isPodcast
        }
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            launchWhenResumed { trySave() }
        }
        cancelButton.setOnClickListener { dismiss() }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
    }

    private suspend fun trySave(){
        val result = editItemViewModel.updateArtist(
            UpdateArtistInfo(
                mediaId,
                artist.extractText().trim(),
                albumArtist.extractText().trim(),
                podcast.isChecked
            )
        )

        when (result){
            UpdateResult.OK -> dismiss()
            UpdateResult.EMPTY_TITLE -> toast(localization.R.string.edit_artist_invalid_title)
            else -> {}
        }
    }

    override fun onLoaderCancelled() {
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_artist
}