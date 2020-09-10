package dev.olog.feature.edit.album

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
import dev.olog.feature.edit.model.UpdateAlbumInfo
import dev.olog.feature.edit.model.UpdateResult
import dev.olog.lib.audio.tagger.model.Tags
import dev.olog.shared.android.extensions.extractText
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class EditAlbumFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditAlbumFragment"
        const val ARGUMENTS_MEDIA_ID = "${TAG}_arguments_media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId.Category): EditAlbumFragment {
            return EditAlbumFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId.toPresentation()
            )
        }
    }

    private val viewModel by viewModels<EditAlbumFragmentViewModel>()

    private val editItemViewModel by activityViewModels<EditItemViewModel>()
    private val mediaId by lazyFast {
        getArgument<PresentationId.Category>(ARGUMENTS_MEDIA_ID)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.requestData(mediaId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        album.afterTextChange()
//            .map { it.isNotBlank() }
//            .onEach { okButton.isEnabled = it }
//            .launchIn(viewLifecycleOwner.lifecycleScope)

        loadImage(mediaId)

        viewModel.observeData()
            .onEach {
//                album.setText(it.title)
//                artist.setText(it.artist)
//                albumArtist.setText(it.albumArtist)
//                year.setText(it.year)
//                genre.setText(it.genre)
//                val text = resources.getQuantityString(
//                    R.plurals.edit_item_xx_tracks_will_be_updated, it.songs, it.songs
//                )
//                albumsUpdated.text = text
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
//        okButton.onClick { trySave() }
//        cancelButton.setOnClickListener { dismiss() }
    }

    override fun onPause() {
        super.onPause()
//        okButton.setOnClickListener(null)
//        cancelButton.setOnClickListener(null)
    }

    private suspend fun trySave() {
//        val result = editItemViewModel.updateAlbum(
//            UpdateAlbumInfo(
//                mediaId = mediaId,
//                tags = Tags(
//                    album = album.extractText().trim(),
//                    artist = artist.extractText().trim(),
//                    albumArtist = albumArtist.extractText().trim(),
//                    genre = genre.extractText().trim(),
//                    year = year.extractText().trim()
//                )
//            )
//        )
//
//        when (result) {
//            UpdateResult.OK -> dismiss()
//            UpdateResult.EMPTY_TITLE -> requireContext().toast(R.string.edit_song_invalid_title)
//            UpdateResult.ILLEGAL_YEAR -> requireContext().toast(R.string.edit_song_invalid_year)
//            UpdateResult.ILLEGAL_DISC_NUMBER,
//            UpdateResult.ILLEGAL_TRACK_NUMBER -> {
//            }
//        }
    }

    override fun onLoaderCancelled() {
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_album
}