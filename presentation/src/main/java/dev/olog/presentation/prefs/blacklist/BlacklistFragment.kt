package dev.olog.presentation.prefs.blacklist

import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.presentation.base.ListDialog
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.lazyFast

@AndroidEntryPoint
class BlacklistFragment : ListDialog() {

    companion object {
        const val TAG = "BlacklistFragment"

        fun newInstance(): BlacklistFragment {
            return BlacklistFragment()
        }
    }

    private val viewModel by viewModels<BlacklistFragmentViewModel>()

    private val adapter by lazyFast {
        BlacklistFragmentAdapter(viewModel::updateToggleState)
    }

    // TODO refactor to normal fragment, reactive is not working in the dialog
    override fun setupBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder
            .setTitle(R.string.prefs_blacklist_title)
            .setMessage(R.string.prefs_blacklist_description)
            .setNegativeButton(R.string.popup_negative_cancel, null)
            .setPositiveButton(R.string.popup_positive_save, null)
    }

    override fun setupRecyclerView(list: RecyclerView) {
        list.adapter = adapter
        list.layoutManager = GridLayoutManager(context, 3)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.liveData.observe(this) {
            adapter.submitList(it)
        }
    }

    override fun positiveAction() {
        val allIsBlacklisted = viewModel.data.all { it.isBlacklisted }
        if (allIsBlacklisted){
            showErrorMessage()
        } else {
            viewModel.saveBlacklisted(viewModel.data)
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