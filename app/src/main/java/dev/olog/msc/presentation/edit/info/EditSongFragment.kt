package dev.olog.msc.presentation.edit.info

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.app.GlideApp
import dev.olog.msc.presentation.BindingsAdapter
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.edit.info.model.DisplayableSong
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.withArguments
import kotlinx.android.synthetic.main.fragment_edit_info.*
import kotlinx.android.synthetic.main.fragment_edit_info.view.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class EditSongFragment : BaseFragment() {

    companion object {
        const val TAG = "EditInfoFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        fun newInstance(mediaId: MediaId): EditSongFragment {
            return EditSongFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString())
        }
    }

    @Inject lateinit var viewModel: EditSongFragmentViewModel

    private var progressDialog: ProgressDialog? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        RxTextView.afterTextChangeEvents(title)
                .map { it.view().text.toString() }
                .map { it.isNotBlank() }
                .asLiveData()
                .subscribe(this, { okButton.isEnabled = it })

        viewModel.observeData()
                .observe(this, Observer {
                    if (it != null){
                        title.setText(it.title)
                        artist.setText(it.artist)
                        album.setText(it.album)
                        year.setText(it.year)
                        genre.setText(it.genre)
                        disc.setText(it.disc)
                        trackNumber.setText(it.track)
                        setImage(it)
                    } else {
                        context!!.toast(R.string.popup_error_message)
                    }
                    hideLoader()
                })
    }

    override fun onResume() {
        super.onResume()
        view!!.okButton.setOnClickListener {}
        view!!.cancelButton.setOnClickListener { activity!!.onBackPressed() }
        view!!.autoTag.setOnClickListener {
            viewModel.fetchSongInfo()
            showLoader()
        }
    }

    override fun onPause() {
        super.onPause()
        view!!.okButton.setOnClickListener(null)
        view!!.cancelButton.setOnClickListener(null)
        view!!.autoTag.setOnClickListener(null)
    }

    private fun setImage(song: DisplayableSong){
        GlideApp.with(context!!).clear(cover)

        GlideApp.with(context!!)
                .load(Uri.parse(song.image))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(BindingsAdapter.OVERRIDE_BIG)
                .priority(Priority.IMMEDIATE)
                .placeholder(CoverUtils.getGradient(context!!, song.id.toInt()))
                .into(cover)
    }

    private fun showLoader(){
        progressDialog = ProgressDialog.show(context, "", "Fetching song info", true)
        progressDialog?.setCancelable(true)
        progressDialog?.setCanceledOnTouchOutside(true)
    }

    private fun hideLoader(){
        progressDialog?.dismiss()
        progressDialog = null
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_info
}