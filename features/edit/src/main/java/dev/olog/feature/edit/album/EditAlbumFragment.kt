package dev.olog.feature.edit.album

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
import kotlinx.android.synthetic.main.fragment_edit_album.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
internal class EditAlbumFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditAlbumFragment"

        fun newInstance(mediaId: MediaId): EditAlbumFragment {
            return EditAlbumFragment().withArguments(
                Params.MEDIA_ID to mediaId.toString()
            )
        }
    }

    private val viewModel by viewModels<EditAlbumFragmentViewModel>()
    private val editItemViewModel by activityViewModels<EditItemViewModel>()

    private val mediaId by argument(Params.MEDIA_ID, MediaId::fromString)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        album.afterTextChange()
            .map { it.isNotBlank() }
            .onEach { okButton.isEnabled = it }
            .launchIn(this)

        loadImage(mediaId)

        viewModel.observeData()
            .onEach {
                album.setText(it.title)
                artist.setText(it.artist)
                albumArtist.setText(it.albumArtist)
                year.setText(it.year)
                genre.setText(it.genre)
                val text = resources.getQuantityString(
                    R.plurals.edit_item_xx_tracks_will_be_updated, it.songs, it.songs)
                albumsUpdated.text =  text
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
        val result = editItemViewModel.updateAlbum(
            UpdateAlbumInfo(
                mediaId,
                album.extractText().trim(),
                artist.extractText().trim(),
                albumArtist.extractText().trim(),
                genre.extractText().trim(),
                year.extractText().trim(),
                podcast.isChecked
            )
        )

        when (result){
            UpdateResult.OK -> dismiss()
            UpdateResult.EMPTY_TITLE -> requireContext().toast(R.string.edit_song_invalid_title)
            UpdateResult.ILLEGAL_YEAR -> requireContext().toast(R.string.edit_song_invalid_year)
            UpdateResult.ILLEGAL_DISC_NUMBER,
            UpdateResult.ILLEGAL_TRACK_NUMBER -> {}
        }
    }

    override fun onLoaderCancelled() {
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_album
}