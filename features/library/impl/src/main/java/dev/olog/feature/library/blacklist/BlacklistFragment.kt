package dev.olog.feature.library.blacklist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.base.ListDialog
import dev.olog.feature.library.blacklist.BlacklistFragmentViewModel.Event
import dev.olog.shared.android.extensions.collectOnLifecycle
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.exhaustive

// TODO rewrite UI
@AndroidEntryPoint
class BlacklistFragment : ListDialog() {

    companion object {
        const val TAG = "BlacklistFragment"
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
        adapter = BlacklistFragmentAdapter(viewModel::toggleBlacklisted)
        list.adapter = adapter
        list.layoutManager = GridLayoutManager(context, 3)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.items.collectOnLifecycle(this) {
            adapter.submitList(it)
        }

        viewModel.events.collectOnLifecycle(this) { event ->
            when (event) {
                Event.Dismiss -> dismiss()
                Event.BlacklistAllError -> showErrorMessage()
            }.exhaustive
        }
    }

    override fun positiveAction() {
        viewModel.onSaveClick()
    }

    private fun showErrorMessage() {
        requireActivity().toast(localization.R.string.prefs_blacklist_error)
    }

}