package dev.olog.presentation.prefs.blacklist

import android.provider.MediaStore
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.feature.base.base.ListDialog
import dev.olog.shared.android.extensions.toast
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.takeWhile

@AndroidEntryPoint
class BlacklistFragment : ListDialog() {

    companion object {
        const val TAG = "BlacklistFragment"

        fun newInstance(): BlacklistFragment {
            return BlacklistFragment()
        }
    }

    private val viewModel by viewModels<BlacklistFragmentViewModel>()

    private lateinit var adapter: BlacklistFragmentAdapter

    override fun setupBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder
            .setTitle(R.string.prefs_blacklist_title)
            .setMessage(R.string.prefs_blacklist_description)
            .setNegativeButton(R.string.popup_negative_cancel, null)
            .setPositiveButton(R.string.popup_positive_save, null)
    }

    override fun setupRecyclerView(list: RecyclerView) {
        // TODo check if works
        viewModel.data.takeWhile { it.isNotEmpty() }
            .take(1)
            .onEach { data ->
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
        val contentResolver = requireContext().contentResolver
        contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
        contentResolver.notifyChange(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null)
        contentResolver.notifyChange(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, null)
    }

    private fun showErrorMessage(){
        requireActivity().toast(R.string.prefs_blacklist_error)
    }

}