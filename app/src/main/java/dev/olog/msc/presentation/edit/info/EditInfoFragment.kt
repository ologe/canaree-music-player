package dev.olog.msc.presentation.edit.info

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.presentation.BindingsAdapter
import dev.olog.msc.presentation.GlideApp
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.withArguments
import kotlinx.android.synthetic.main.fragment_edit_info.*
import kotlinx.android.synthetic.main.fragment_edit_info.view.*
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jetbrains.anko.toast
import java.io.File
import javax.inject.Inject

class EditInfoFragment : BaseFragment(), EditInfoFragmentView {

    companion object {
        const val TAG = "EditInfoFragment"
        const val ARGUMENTS_MEDIA_ID = "${TAG}.arguments.media_id"

        fun newInstance(mediaId: MediaId): EditInfoFragment {
            return EditInfoFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString())
        }
    }

    @Inject lateinit var presenter: EditInfoFragmentPresenter
    @Inject lateinit var autoTag: AutoTag

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        RxTextView.afterTextChangeEvents(title)
                .map { it.view().text.toString() }
                .map { it.isNotBlank() }
                .asLiveData()
                .subscribe(this, { okButton.isEnabled = it })
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        presenter.getSong().subscribe({
                    setImage(view, it)
                    setTextViews(view, it)
                }, Throwable::printStackTrace)
    }

    override fun onResume() {
        super.onResume()
        view!!.okButton.setOnClickListener {
            if (isDataValid()){
                presenter.updateMediaStore(
                        view!!.title.text.toString(),
                        view!!.artist.text.toString(),
                        view!!.album.text.toString(),
                        view!!.year.text.toString(),
                        view!!.genre.text.toString(),
                        view!!.disc.text.toString(),
                        view!!.trackNumber.text.toString()
                )
                activity!!.onBackPressed()
            } else {
                if (!TextUtils.isDigitsOnly(view!!.disc.text)){
                    showToast(R.string.edit_info_disc_number_not_digits)
                } else if (!TextUtils.isDigitsOnly(view!!.trackNumber.text)) {
                    showToast(R.string.edit_info_track_number_not_digits)
                }
            }
        }
        view!!.cancelButton.setOnClickListener { activity!!.onBackPressed() }
        view!!.autoTag.setOnClickListener { autoTag.getTags() }
    }

    override fun onPause() {
        super.onPause()
        view!!.okButton.setOnClickListener(null)
        view!!.cancelButton.setOnClickListener(null)
        view!!.autoTag.setOnClickListener(null)
    }

    private fun isDataValid(): Boolean {
        return view!!.title.text.isNotBlank() &&
                TextUtils.isDigitsOnly(view!!.disc.text) &&
                TextUtils.isDigitsOnly(view!!.trackNumber.text)
    }

    private fun setImage(view: View, song: Song){
        GlideApp.with(context!!).clear(view)

        GlideApp.with(context!!)
                .load(Uri.parse(song.image))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(BindingsAdapter.OVERRIDE_BIG)
                .priority(Priority.IMMEDIATE)
                .error(CoverUtils.getGradient(context!!, song.id.toInt()))
                .into(view.cover)
    }

    private fun setTextViews(view: View, song: Song){
        val file = File(song.path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        view.title.setText(song.title)

        val artist = tag.getFirst(FieldKey.ARTIST)
        if (artist != AppConstants.UNKNOWN){
            view.artist.setText(artist)
        }

        val album = tag.getFirst(FieldKey.ALBUM)
        if (album != AppConstants.UNKNOWN){
            view.album.append(album)
        }

        val year = tag.getFirst(FieldKey.YEAR)
        view.year.setText(year)

        val genre = tag.getFirst(FieldKey.GENRE)
        view.genre.setText(genre)

        val discNumber = tag.getFirst(FieldKey.DISC_NO)
        view.disc.setText(discNumber)

        val trackNumber = tag.getFirst(FieldKey.TRACK)
        view.trackNumber.setText(trackNumber)
    }

    override fun toggleLoading(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        view!!.loading.visibility = visibility
    }

    override fun showToast(message: String) {
        activity!!.toast(message)
    }

    override fun showToast(stringRes: Int) {
        showToast(getString(stringRes))
    }

    override fun setTitle(title: String) {
        view!!.title.text.clear()
        view!!.title.setText(title)
    }

    override fun setArtist(artist: String) {
        view!!.artist.text.clear()
        view!!.artist.setText(artist)
    }

    override fun setAlbum(album: String) {
        view!!.album.text.clear()
        view!!.album.setText(album)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_info
}