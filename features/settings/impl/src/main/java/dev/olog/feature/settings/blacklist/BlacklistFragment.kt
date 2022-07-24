package dev.olog.feature.settings.blacklist

import android.provider.MediaStore
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.platform.navigation.FragmentTagFactory
import dev.olog.shared.extension.launchWhenResumed
import dev.olog.shared.extension.toast
import dev.olog.ui.fragment.ListDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class BlacklistFragment : ListDialog() {

    companion object {
        val TAG = FragmentTagFactory.create(BlacklistFragment::class)

        fun newInstance(): BlacklistFragment {
            return BlacklistFragment()
        }
    }

    private val viewModel by viewModels<BlacklistFragmentViewModel>()

    private lateinit var adapter: BlacklistFragmentAdapter

    override fun setupBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder
            .setTitle(localization.R.string.prefs_blacklist_title)
            .setMessage(localization.R.string.prefs_blacklist_description)
            .setNegativeButton(localization.R.string.popup_negative_cancel, null)
            .setPositiveButton(localization.R.string.popup_positive_save, null)
    }

    override fun setupRecyclerView(list: RecyclerView) {
        launchWhenResumed {
            val data = withContext(Dispatchers.Default) {
                viewModel.data()
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
            viewModel.saveBlacklisted(adapter.getData())
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
        activity!!.toast(localization.R.string.prefs_blacklist_error)
    }

}