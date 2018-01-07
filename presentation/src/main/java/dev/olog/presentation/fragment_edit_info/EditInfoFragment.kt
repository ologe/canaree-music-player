package dev.olog.presentation.fragment_edit_info

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.domain.entity.UneditedSong
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.extension.withArguments
import dev.olog.shared.MediaId
import dev.olog.shared_android.Constants
import dev.olog.shared_android.CoverUtils
import dev.olog.shared_android.extension.asLiveData
import kotlinx.android.synthetic.main.fragment_edit_info.*
import kotlinx.android.synthetic.main.fragment_edit_info.view.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class EditInfoFragment : BaseFragment(), EditInfoFragmentView {

    companion object {
        const val TAG = "EditInfoFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: MediaId): EditInfoFragment {
            return EditInfoFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString())
        }
    }

    @Inject lateinit var presenter: EditInfoFragmentPresenter
    @Inject lateinit var autoTag: AutoTag

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        RxTextView.afterTextChangeEvents(first)
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
            presenter.updateMediaStore(
                    view!!.first.text.toString(),
                    view!!.second.text.toString(),
                    view!!.third.text.toString()
            )
            activity!!.onBackPressed()
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

    private fun setImage(view: View, song: UneditedSong){
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

    private fun setTextViews(view: View, song: UneditedSong){
        view.first.append(song.title)
        val artist = song.artist
        if (artist != Constants.UNKNOWN){
            view.second.append(artist)
        }
        val album = song.album
        if (album != Constants.UNKNOWN){
            view.third.append(album)
        }
    }

    override fun toggleLoading(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        view!!.loading.visibility = visibility
    }

    override fun showToast(message: String) {
        activity!!.toast(message)
    }

    override fun setTitle(title: String) {
        view!!.first.text.clear()
        view!!.first.append(title)
    }

    override fun setArtist(artist: String) {
        view!!.second.text.clear()
        view!!.second.append(artist)
    }

    override fun setAlbum(album: String) {
        view!!.third.text.clear()
        view!!.third.append(album)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_info
}