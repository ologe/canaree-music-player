package dev.olog.feature.edit.album

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.feature.edit.BaseEditItemFragment
import dev.olog.feature.edit.EditItemViewModel
import dev.olog.feature.edit.R
import dev.olog.feature.edit.UpdateAlbumInfo
import dev.olog.feature.edit.UpdateResult
import dev.olog.shared.extension.afterTextChange
import dev.olog.shared.extension.argument
import dev.olog.shared.extension.collectOnViewLifecycle
import dev.olog.shared.extension.extractText
import dev.olog.shared.extension.launchWhenResumed
import dev.olog.shared.extension.subscribe
import dev.olog.shared.extension.toast
import dev.olog.shared.extension.withArguments
import kotlinx.android.synthetic.main.fragment_edit_album.*
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class EditAlbumFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditAlbumFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): EditAlbumFragment {
            return EditAlbumFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId
            )
        }
    }

    private val viewModel by viewModels<EditAlbumFragmentViewModel>()
    private val editItemViewModel by activityViewModels<EditItemViewModel>()
    private val mediaId: MediaId by argument<MediaId>(ARGUMENTS_MEDIA_ID)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.requestData(mediaId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        album.afterTextChange()
            .map { it.isNotBlank() }
            .collectOnViewLifecycle(this) { okButton.isEnabled = it }

        loadImage(mediaId)

        viewModel.observeData().subscribe(viewLifecycleOwner) {
            album.setText(it.title)
            artist.setText(it.artist)
            albumArtist.setText(it.albumArtist)
            year.setText(it.year)
            genre.setText(it.genre)
            val text = resources.getQuantityString(
                localization.R.plurals.edit_item_xx_tracks_will_be_updated, it.songs, it.songs)
            albumsUpdated.text =  text
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
            UpdateResult.EMPTY_TITLE -> toast(localization.R.string.edit_song_invalid_title)
            UpdateResult.ILLEGAL_YEAR -> toast(localization.R.string.edit_song_invalid_year)
            UpdateResult.ILLEGAL_DISC_NUMBER,
            UpdateResult.ILLEGAL_TRACK_NUMBER -> {}
        }
    }

    override fun onLoaderCancelled() {
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_album
}