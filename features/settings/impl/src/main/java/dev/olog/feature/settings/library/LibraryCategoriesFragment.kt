package dev.olog.feature.settings.library

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaIdCategory
import dev.olog.platform.adapter.drag.DragListenerImpl
import dev.olog.platform.adapter.drag.IDragListener
import dev.olog.shared.extension.argument
import dev.olog.shared.extension.lazyFast
import dev.olog.shared.extension.withArguments
import dev.olog.ui.adapter.drag.CircularRevealAnimationController
import dev.olog.ui.fragment.ListDialog

@AndroidEntryPoint
class LibraryCategoriesFragment : ListDialog(), IDragListener by DragListenerImpl() {

    companion object {
        const val TAG = "LibraryCategoriesFragment"
        const val TYPE = "$TAG.TYPE"

        @JvmStatic
        fun newInstance(category: MediaIdCategory): LibraryCategoriesFragment {
            return LibraryCategoriesFragment().withArguments(
                TYPE to category
            )
        }
    }

    private val viewModel by viewModels<LibraryCategoriesFragmentViewModel>()
    private val adapter by lazyFast {
        LibraryCategoriesFragmentAdapter(viewModel.getDataSet(category), this)
    }

    private val category by argument<MediaIdCategory>(TYPE)

    override fun setupBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        val title = if (category == MediaIdCategory.SONGS) localization.R.string.prefs_library_categories_title else localization.R.string.prefs_podcast_library_categories_title

        return builder.setTitle(title)
            .setNeutralButton(localization.R.string.popup_neutral_reset, null)
            .setNegativeButton(localization.R.string.popup_negative_cancel, null)
            .setPositiveButton(localization.R.string.popup_positive_save, null)
    }

    override fun setupRecyclerView(list: RecyclerView) {
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)
        setupDragListener(
            scope = viewLifecycleOwner.lifecycleScope,
            list = list,
            direction = 0,
            animation = CircularRevealAnimationController(),
        )
    }

    override fun positiveAction() {
        viewModel.setDataSet(category, adapter.getData())
        requireActivity().recreate()
        dismiss()
    }

    override fun neutralAction() {
        val defaultData = viewModel.getDefaultDataSet(category)
        adapter.updateDataSet(defaultData)
    }

}