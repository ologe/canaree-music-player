package dev.olog.presentation.prefs.blacklist

import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.R
import dev.olog.presentation.base.ListDialog
import dev.olog.shared.extensions.toast
import kotlinx.coroutines.*
import javax.inject.Inject

class BlacklistFragment : ListDialog() {

    companion object {
        const val TAG = "BlacklistFragment"

        fun newInstance(): BlacklistFragment {
            return BlacklistFragment()
        }
    }

    @Inject lateinit var presenter: BlacklistFragmentPresenter

    private lateinit var adapter: BlacklistFragmentAdapter

    override fun setupBuilder(builder: AlertDialog.Builder): AlertDialog.Builder {
        return builder
            .setTitle(R.string.prefs_blacklist_title)
            .setMessage(R.string.prefs_blacklist_description)
            .setNegativeButton(R.string.popup_negative_cancel, null)
            .setPositiveButton(R.string.popup_positive_save, null)
    }

    override fun setupRecyclerView(list: RecyclerView) {
        GlobalScope.launch {
            adapter = BlacklistFragmentAdapter(presenter.data)
            list.adapter = adapter
            list.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun positiveAction() {
        val allIsBlacklisted = adapter.getData().all { it.isBlacklisted }
        if (allIsBlacklisted){
            showErrorMessage()
        } else {
            presenter.saveBlacklisted(adapter.getData())
            notifyMediaStore()
            dismiss()
        }
    }

    private fun notifyMediaStore(){
        val contentResolver = context!!.contentResolver
        contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
        contentResolver.notifyChange(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null)
        contentResolver.notifyChange(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, null)
    }

    private fun showErrorMessage(){
        activity!!.toast(R.string.prefs_blacklist_error)
    }

}