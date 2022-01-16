package dev.olog.feature.library.category

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaStoreType
import dev.olog.feature.base.ListDialog
import dev.olog.feature.base.drag.DragListenerImpl
import dev.olog.feature.base.drag.IDragListener
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.getArgument
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import javax.inject.Inject

@AndroidEntryPoint
class LibraryCategoriesFragment : ListDialog(), IDragListener by DragListenerImpl() {

    companion object {
        const val CATEGORY = "category"

        @JvmStatic
        fun newInstance(category: MediaStoreType): LibraryCategoriesFragment {
            return LibraryCategoriesFragment().withArguments(CATEGORY to category)
        }
    }

    @Inject
    internal lateinit var presenter: LibraryCategoriesFragmentPresenter
    private val adapter by lazyFast {
        LibraryCategoriesFragmentAdapter(presenter.getDataSet(category), this)
    }

    private val category by lazyFast { getArgument<MediaStoreType>(CATEGORY) }

    override fun setupBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        val title = when (category) {
            MediaStoreType.Song -> localization.R.string.prefs_library_categories_title
            MediaStoreType.Podcast -> localization.R.string.prefs_podcast_library_categories_title
        }

        return builder.setTitle(title)
            .setNeutralButton(localization.R.string.popup_neutral_reset, null)
            .setNegativeButton(localization.R.string.popup_negative_cancel, null)
            .setPositiveButton(localization.R.string.popup_positive_save, null)
    }

    override fun setupRecyclerView(list: RecyclerView) {
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)
//        setupDragListener(list, 0) todo
    }

    override fun positiveAction() {
        presenter.setDataSet(category, adapter.getData())
        act.recreate()
        dismiss()
    }

    override fun neutralAction() {
        val defaultData = presenter.getDefaultDataSet(category)
        adapter.updateDataSet(defaultData)
    }

}