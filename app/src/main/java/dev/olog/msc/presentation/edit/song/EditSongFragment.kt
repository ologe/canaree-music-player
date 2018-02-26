package dev.olog.msc.presentation.edit.song

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
import dev.olog.msc.app.GlideApp
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.edit.song.model.UpdateResult
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.*
import kotlinx.android.synthetic.main.fragment_edit_song.*
import javax.inject.Inject

private const val RESULT_LOAD_IMAGE = 12346

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
                UpdateResult.OK -> {
                    activity!!.onBackPressed()
                }
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

            val list = arrayOf(
                    "Fetch from LastFm",
                    "Choose from local images",
                    "Restore original"
            )

            AlertDialog.Builder(ctx)
                    .setItems(list, { _, which ->
                        when (which){
                            0 -> {
                                viewModel.fetchAlbumArt()
                                showLoader("Fetching image")
                            }
                            1 -> {
                                val intent = Intent(Intent.ACTION_PICK)
                                intent.type = "image/*"
                                act.startActivityForResult(intent, RESULT_LOAD_IMAGE)
                            }
                            2 -> {
                                viewModel.restoreAlbumArt()
                            }
                        }
                    })
                    .makeDialog()
        }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
        autoTag.setOnClickListener(null)
        changeAlbumArt.setOnClickListener(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println(data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == RESULT_LOAD_IMAGE){
                data?.data?.let {
                    viewModel.setAlbumArt(it)
                }
            }
        }
    }

    private fun setImage(string: String){
        GlideApp.with(ctx).clear(cover)
        GlideApp.with(ctx).clear(backgroundCover)

        val builder = GlideApp.with(ctx)
                .load(string)
                .error(GlideApp.with(ctx)
                        .load(Uri.parse(string))
                        .placeholder(CoverUtils.getGradient(ctx, viewModel.getSongId()))
                ).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(500)
                .priority(Priority.IMMEDIATE)

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

    override fun provideLayoutId(): Int = R.layout.fragment_edit_song
}