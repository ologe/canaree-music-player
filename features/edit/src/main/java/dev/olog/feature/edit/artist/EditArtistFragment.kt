package dev.olog.feature.edit.artist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.mediaid.MediaId
import dev.olog.feature.edit.*
import dev.olog.feature.edit.BaseEditItemFragment
import dev.olog.feature.edit.EditItemViewModel
import dev.olog.feature.edit.UpdateResult
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.*
import kotlinx.android.synthetic.main.fragment_edit_artist.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
internal class EditArtistFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditArtistFragment"

        fun newInstance(mediaId: MediaId): EditArtistFragment {
            return EditArtistFragment().withArguments(
                Params.MEDIA_ID to mediaId.toString()
            )
        }
    }

    private val viewModel by viewModels<EditArtistFragmentViewModel>()
    private val editItemViewModel by activityViewModels<EditItemViewModel>()

    private val mediaId by argument(Params.MEDIA_ID, MediaId::fromString)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        artist.afterTextChange()
            .map { it.isNotBlank() }
            .onEach { okButton.isEnabled = it }
            .launchIn(this)

        loadImage(mediaId)

        viewModel.observeData()
            .onEach {
                artist.setText(it.title)
                albumArtist.setText(it.albumArtist)
                val text = resources.getQuantityString(
                    R.plurals.edit_item_xx_tracks_will_be_updated, it.songs, it.songs
                )
                albumsUpdated.text = text
                podcast.isChecked = it.isPodcast
            }.launchIn(this)
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            launch { trySave() }
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
            UpdateResult.EMPTY_TITLE -> requireContext().toast(R.string.edit_artist_invalid_title)
            else -> {}
        }
    }

    override fun onLoaderCancelled() {
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_artist
}