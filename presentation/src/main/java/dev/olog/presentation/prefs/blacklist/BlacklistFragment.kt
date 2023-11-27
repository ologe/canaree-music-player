package dev.olog.presentation.prefs.blacklist

import android.provider.MediaStore
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.presentation.base.ListDialog
import dev.olog.shared.android.extensions.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class BlacklistFragment : ListDialog() {

    companion object {
        const val TAG = "BlacklistFragment"

        fun newInstance(): BlacklistFragment {
            return BlacklistFragment()
        }
    }

    @Inject lateinit var presenter: BlacklistFragmentPresenter

    private lateinit var adapter: BlacklistFragmentAdapter

    override fun setupBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder
            .setTitle(R.string.prefs_blacklist_title)
            .setMessage(R.string.prefs_blacklist_description)
            .setNegativeButton(R.string.popup_negative_cancel, null)
            .setPositiveButton(R.string.popup_positive_save, null)
    }

    override fun setupRecyclerView(list: RecyclerView) {
        GlobalScope.launch(Dispatchers.Main) {
            val data = withContext(Dispatchers.Default) {
                presenter.data
            }
            adapter = BlacklistFragmentAdapter(data)
            list.adapter = adapter
            list.layoutManager = GridLayoutManager(context, 3)
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