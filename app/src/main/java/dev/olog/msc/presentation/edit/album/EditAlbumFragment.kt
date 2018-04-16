package dev.olog.msc.presentation.edit.album

import android.arch.lifecycle.Observer
import android.os.Bundle
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.presentation.edit.BaseEditItemFragment
import dev.olog.msc.presentation.edit.UpdateResult
import dev.olog.msc.presentation.model.DisplayableItem
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
                    val model = DisplayableItem(0, MediaId.albumId(it.id), "", image = it.image)
                    setImage(model)
                }
            }
            hideLoader()
        })
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
                UpdateResult.OK -> {
                    ctx.toast(R.string.edit_album_update_success)
                    act.onBackPressed()
                }
                UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_song_invalid_title)
                UpdateResult.ILLEGAL_YEAR -> ctx.toast(R.string.edit_song_invalid_year)
                UpdateResult.ERROR -> ctx.toast(R.string.popup_error_message)
                else -> throw IllegalArgumentException("invalid result $result")
            }
        }
        cancelButton.setOnClickListener { act.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
    }

    override fun onLoaderCancelled() {
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_album
}