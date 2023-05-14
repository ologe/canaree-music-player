package dev.olog.presentation.prefs.categories

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.R
import dev.olog.presentation.base.ListDialog
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.platform.extension.act
import dev.olog.platform.extension.withArguments
import dev.olog.shared.lazyFast

@AndroidEntryPoint
class LibraryCategoriesFragment : ListDialog(), IDragListener by DragListenerImpl() {

    companion object {
        const val TAG = "LibraryCategoriesFragment"
        const val IS_PODCAST = "$TAG.IS_PODCAST"

        fun newInstance(isPodcast: Boolean): LibraryCategoriesFragment {
            return LibraryCategoriesFragment().withArguments(
                IS_PODCAST to isPodcast
            )
        }
    }

    private val viewModel by viewModels<LibraryCategoriesFragmentViewModel>()
    private val adapter by lazyFast {
        LibraryCategoriesFragmentAdapter(viewModel.getDataSet(isPodcast), this)
    }

    private val isPodcast: Boolean
        get() = arguments!!.getBoolean(IS_PODCAST)

    override fun setupBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        val title = if (isPodcast) R.string.prefs_library_categories_title else R.string.prefs_podcast_library_categories_title

        return builder.setTitle(title)
            .setNeutralButton(R.string.popup_neutral_reset, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
            .setPositiveButton(R.string.popup_positive_save, null)
    }

    override fun setupRecyclerView(list: RecyclerView) {
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)
        setupDragListener(this, list, 0)
    }

    override fun positiveAction() {
        viewModel.setDataSet(isPodcast, adapter.getData())
        act.recreate()
        dismiss()
    }

    override fun neutralAction() {
        val defaultData = viewModel.getDefaultDataSet(isPodcast)
        adapter.updateDataSet(defaultData)
    }

}