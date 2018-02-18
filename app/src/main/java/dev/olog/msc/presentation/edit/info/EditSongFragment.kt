package dev.olog.msc.presentation.edit.info

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.os.Bundle
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.presentation.BindingsAdapter
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.edit.info.model.UpdateResult
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.extractText
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.withArguments
import kotlinx.android.synthetic.main.fragment_edit_info.*
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
                    } else {
                        context!!.toast("info not found")
                    }
                    hideLoader()
                })

        viewModel.observeImage()
                .observe(this, Observer {
                    if (it != null){
                        setImage(it)
                    } else {
                        context!!.toast("image not found")
                    }
                    hideLoader()
                })

        viewModel.observeConnectivity()
                .asLiveData()
                .subscribe(this, { context!!.toast(it) })
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            val result = viewModel.updateMetadata(title.extractText(), artist.extractText(),
                            album.extractText(), genre.extractText(), year.extractText(),
                            disc.extractText(), trackNumber.extractText())

            when (result){
                UpdateResult.OK -> activity!!.onBackPressed()
                UpdateResult.EMPTY_TITLE -> context!!.toast("title can not be null")
                UpdateResult.ILLEGAL_DISC_NUMBER -> context!!.toast("invalid disc number")
                UpdateResult.ILLEGAL_TRACK_NUMBER -> context!!.toast("invalid track number")
                UpdateResult.ILLEGAL_YEAR -> context!!.toast("invalid year")
                UpdateResult.ERROR -> context!!.toast(R.string.popup_error_message)
            }
        }
        cancelButton.setOnClickListener { activity!!.onBackPressed() }
        autoTag.setOnClickListener {
            viewModel.fetchSongInfo()
            showLoader("Fetching song info")
        }
        changeAlbumArt.setOnClickListener {
            viewModel.fetchAlbumArt()
            showLoader("Fetching image")
        }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
        autoTag.setOnClickListener(null)
        changeAlbumArt.setOnClickListener(null)
    }

    private fun setImage(string: String){
        GlideApp.with(context!!).clear(cover)

        val builder = GlideApp.with(context!!)
                .load(string)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(BindingsAdapter.OVERRIDE_BIG)
                .priority(Priority.IMMEDIATE)
                .placeholder(CoverUtils.getGradient(context!!, viewModel.getSongId()))

        builder.into(cover)
        builder.into(backgroundCover)
    }

    private fun showLoader(message: String){
        progressDialog = ProgressDialog.show(context, "", message, true)
        progressDialog?.setCancelable(true)
        progressDialog?.setCanceledOnTouchOutside(true)
        progressDialog?.setOnCancelListener {
            viewModel.stopFetching()
            progressDialog?.setOnCancelListener(null)
        }
    }

    private fun hideLoader(){
        progressDialog?.dismiss()
        progressDialog = null
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_info
}