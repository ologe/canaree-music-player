package dev.olog.presentation.edit.album

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.platform.extension.afterTextChange
import dev.olog.platform.extension.ctx
import dev.olog.platform.extension.extractText
import dev.olog.platform.extension.toast
import dev.olog.platform.extension.withArguments
import dev.olog.presentation.R
import dev.olog.presentation.edit.BaseEditItemFragment
import dev.olog.presentation.edit.EditItemViewModel
import dev.olog.presentation.edit.UpdateAlbumInfo
import dev.olog.presentation.edit.model.UpdateResult
import dev.olog.shared.lazyFast
import dev.olog.shared.subscribe
import kotlinx.android.synthetic.main.fragment_edit_album.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditAlbumFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditAlbumFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: MediaId): EditAlbumFragment {
            return EditAlbumFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString())
        }
    }

    private val viewModel by viewModels<EditAlbumFragmentViewModel>()
    private val editItemViewModel by activityViewModels<EditItemViewModel>()
    private val mediaId: MediaId by lazyFast {
        MediaId.fromString(getArgument(ARGUMENTS_MEDIA_ID))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.requestData(mediaId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            album.afterTextChange()
                .map { it.isNotBlank() }
                .collect { okButton.isEnabled = it }
        }

        loadImage(mediaId)

        viewModel.observeData().subscribe(viewLifecycleOwner) {
            album.setText(it.title)
            artist.setText(it.artist)
            albumArtist.setText(it.albumArtist)
            year.setText(it.year)
            genre.setText(it.genre)
            val text = resources.getQuantityString(
                R.plurals.edit_item_xx_tracks_will_be_updated, it.songs, it.songs)
            albumsUpdated.text =  text
            podcast.isChecked = it.isPodcast
        }
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch { trySave() }
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
            UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_song_invalid_title)
            UpdateResult.ILLEGAL_YEAR -> ctx.toast(R.string.edit_song_invalid_year)
            UpdateResult.ILLEGAL_DISC_NUMBER,
            UpdateResult.ILLEGAL_TRACK_NUMBER -> {}
        }
    }

    override fun onLoaderCancelled() {
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_album
}