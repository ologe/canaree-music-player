package dev.olog.msc.presentation.edit.album

import android.arch.lifecycle.Observer
import android.net.Uri
import android.os.Bundle
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.presentation.edit.BaseEditItemFragment
import dev.olog.msc.presentation.edit.UpdateResult
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.*
import kotlinx.android.synthetic.main.fragment_edit_album.*
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

    @Inject lateinit var viewModel: EditAlbumFragmentViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        RxTextView.afterTextChangeEvents(album)
                .map { it.view().text.toString() }
                .map { it.isNotBlank() }
                .asLiveData()
                .subscribe(this, okButton::setEnabled)

        viewModel.observeSongList()
                .subscribe(this, {
                    val size = it.size
                    val text = resources.getQuantityString(
                            R.plurals.edit_item_xx_tracks_will_be_updated, size, size)
                    albumsUpdated.text =  text
                })

        viewModel.observeData().observe(this, Observer {
            when (it){
                null -> ctx.toast(R.string.edit_song_info_not_found)
                else -> {
                    album.setText(it.title)
                    artist.setText(it.artist)
                    year.setText(it.year)
                    genre.setText(it.genre)
                }
            }
            hideLoader()
        })

        viewModel.observeImage().observe(this, Observer {
            when (it){
                null -> ctx.toast(R.string.edit_song_image_not_found)
                else -> setImage(it, viewModel.getAlbumId())
            }
            hideLoader()
        })

        viewModel.observeConnectivity()
                .asLiveData()
                .subscribe(this, { ctx.toast(it) })
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            val result = viewModel.updateMetadata(
                    album.extractText(),
                    artist.extractText(),
                    genre.extractText(),
                    year.extractText().trim()
            )

            when (result){
                UpdateResult.OK -> act.onBackPressed()
                UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_song_invalid_title)
                UpdateResult.ILLEGAL_YEAR -> ctx.toast(R.string.edit_info_year)
                UpdateResult.ERROR -> ctx.toast(R.string.popup_error_message)
                else -> throw IllegalArgumentException("invalid result $result")
            }
        }
        cancelButton.setOnClickListener { act.onBackPressed() }
        autoTag.setOnClickListener {
            viewModel.fetchAlbumInfo()
            showLoader(R.string.edit_song_fetching_info)
        }
        changeAlbumArt.setOnClickListener {
            val items = arrayOf(
                    ctx.getString(R.string.edit_item_image_fetch),
                    ctx.getString(R.string.edit_item_image_choose_local),
                    ctx.getString(R.string.edit_item_image_restore)
            )
            showImageChooser(items, { _, which ->
                when (which){
                    0 -> {
                        viewModel.fetchAlbumArt()
                        showLoader(R.string.edit_song_fetching_image)
                    }
                    1 -> loadLocalImage()
                    2 -> viewModel.restoreAlbumArt()
                }
            })
        }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
        autoTag.setOnClickListener(null)
        changeAlbumArt.setOnClickListener(null)
    }

    override fun onLoaderCancelled() {
        viewModel.stopFetching()
    }

    override fun onLocalImageLoaded(uri: Uri) {
        viewModel.setAlbumArt(uri)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_album
}