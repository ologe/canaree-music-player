package dev.olog.feature.library.library.prefs

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.base.adapter.drag.DragListenerImpl
import dev.olog.feature.base.adapter.drag.IDragListener
import dev.olog.feature.base.base.ListDialog
import dev.olog.feature.library.R
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.lazyFast

@AndroidEntryPoint
internal class LibraryPrefsFragment : ListDialog(), IDragListener by DragListenerImpl() {

    private val fragmentViewModel by viewModels<LibraryPrefsFragmentViewModel>()

    private val adapter by lazyFast {
        LibraryPrefsFragmentAdapter(
            data = fragmentViewModel.getDataSet(),
            dragListener = this
        )
    }

    private val isPodcast by argument<Boolean>(Params.IS_PODCAST)

    override fun setupBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
//        val title = if (isPodcast) R.string.prefs_library_categories_title else R.string.prefs_podcast_library_categories_title TODO
        val title = if (isPodcast) "Podcast categories" else "Tracks categories"

        return builder.setTitle(title)
            .setNeutralButton(R.string.popup_neutral_reset, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
            .setPositiveButton(R.string.popup_positive_save, null)
    }

    override fun setupRecyclerView(list: RecyclerView) {
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)
        setupDragListener(list, 0)
    }

    override fun positiveAction() {
        fragmentViewModel.setDataSet(adapter.getData())
        requireActivity().recreate()
        dismiss()
    }

    override fun neutralAction() {
        val defaultData = fragmentViewModel.getDefaultDataSet()
        adapter.updateDataSet(defaultData)
    }

}