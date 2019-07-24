package dev.olog.presentation.edit.album

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import dev.olog.presentation.R
import dev.olog.shared.AppConstants
import dev.olog.presentation.edit.BaseEditItemFragment
import dev.olog.presentation.edit.EditItemViewModel
import dev.olog.presentation.edit.UpdateAlbumInfo
import dev.olog.presentation.edit.UpdateResult
import dev.olog.core.MediaId
import dev.olog.shared.extensions.*
import kotlinx.android.synthetic.main.fragment_edit_album.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditAlbumFragment : BaseEditItemFragment(), CoroutineScope by MainScope() {

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
        viewModelProvider<EditAlbumFragmentViewModel>(
            viewModelFactory
        )
    }
    private val editItemViewModel by lazyFast {
        activity!!.viewModelProvider<EditItemViewModel>(
            viewModelFactory
        )
    }
    private val mediaId: MediaId by lazyFast {
        MediaId.fromString(getArgument(ARGUMENTS_MEDIA_ID))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        launch {
            album.afterTextChange()
                .map { it.isNotBlank() }
                .collect { okButton.isEnabled = it }
        }

        viewModel.observeSongList()
                .subscribe(viewLifecycleOwner) {
                    val size = it.size
                    val text = resources.getQuantityString(
                            R.plurals.edit_item_xx_tracks_will_be_updated, size, size)
                    albumsUpdated.text =  text
                }

//        viewModel.observeData().observe(viewLifecycleOwner, Observer {
//            when (it){
//                null -> ctx.toast(R.string.edit_song_info_not_found)
//                else -> {
//                    album.setText(it.title)
//                    artist.setText(it.artist)
//                    albumArtist.setText(it.albumArtist)
//                    year.setText(it.year)
//                    genre.setText(it.genre)
//                    setImage(MediaId.albumId(it.id)) TODO
//                }
//            }
//            hideLoader()
//        })
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            launch {
                val result = editItemViewModel.updateAlbum(
                    UpdateAlbumInfo(
                        mediaId,
                        album.extractText().trim(),
                        artist.extractText().trim(),
                        albumArtist.extractText().trim(),
                        genre.extractText().trim(),
                        year.extractText().trim(),
                        viewModel.getNewImage()
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
        }
        cancelButton.setOnClickListener { dismiss() }
        picker.setOnClickListener { changeImage() }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
        picker.setOnClickListener(null)
    }

    override fun restoreImage() {
//        val albumId = viewModel.getAlbum().id
//        val uri = ImagesFolderUtils.forAlbum(albumId)
//        viewModel.updateImage(uri) TODO
    }

    override fun onImagePicked(uri: Uri) {
        viewModel.updateImage(uri.toString())
    }

    override fun noImage() {
        viewModel.updateImage(AppConstants.NO_IMAGE)
    }

    override fun onLoaderCancelled() {
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_album
}