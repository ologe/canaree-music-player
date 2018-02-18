package dev.olog.msc.presentation.edit.info

import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.R
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        RxTextView.afterTextChangeEvents(title)
                .map { it.view().text.toString() }
                .map { it.isNotBlank() }
                .asLiveData()
                .subscribe(this, { okButton.isEnabled = it })

        viewModel.observeData()
                .subscribe(this, {
                    title.setText(it.title)
                    artist.setText(it.artist)
                    album.setText(it.album)
                    year.setText(it.year)
                    genre.setText(it.genre)
                    disc.setText(it.disc)
                    trackNumber.setText(it.track)
                    setImage(it)
                })
    }

    override fun onResume() {
        super.onResume()
        view!!.okButton.setOnClickListener {
//            if (isDataValid()){
//                presenter.updateMediaStore(
//                        view!!.title.text.toString(),
//                        view!!.artist.text.toString(),
//                        view!!.album.text.toString(),
//                        view!!.year.text.toString(),
//                        view!!.genre.text.toString(),
//                        view!!.disc.text.toString(),
//                        view!!.trackNumber.text.toString()
//                )
//                activity!!.onBackPressed()
//            } else {
//                if (!TextUtils.isDigitsOnly(view!!.disc.text)){
//                    showToast(R.string.edit_info_disc_number_not_digits)
//                } else if (!TextUtils.isDigitsOnly(view!!.trackNumber.text)) {
//                    showToast(R.string.edit_info_track_number_not_digits)
//                }
//            }
        }
        view!!.cancelButton.setOnClickListener { activity!!.onBackPressed() }
        view!!.autoTag.setOnClickListener { viewModel.fetchSongInfo() }
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

//    override fun toggleLoading(show: Boolean) {
//        val visibility = if (show) View.VISIBLE else View.GONE
//        view!!.loading.visibility = visibility
//    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_info
}