package dev.olog.presentation.prefs.categories

import android.content.DialogInterface
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.domain.MediaIdCategory
import dev.olog.presentation.R
import dev.olog.presentation.base.ListDialog
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.shared.android.extensions.getArgument
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import javax.inject.Inject

class LibraryCategoriesFragment : ListDialog(), IDragListener by DragListenerImpl() {

    companion object {
        const val TAG = "LibraryCategoriesFragment"
        private const val TYPE = "${TAG}_TYPE"

        @JvmStatic
        fun newInstance(category: MediaIdCategory): LibraryCategoriesFragment {
            return LibraryCategoriesFragment().withArguments(
                TYPE to category.ordinal
            )
        }
    }

    @Inject
    internal lateinit var presenter: LibraryCategoriesFragmentPresenter
    private val adapter by lazyFast {
        LibraryCategoriesFragmentAdapter(this)
    }

    private val category by lazyFast {
        MediaIdCategory.values()[getArgument(TYPE)]
    }

    override fun setupBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        val title = if (category == MediaIdCategory.SONGS) R.string.prefs_library_categories_title else R.string.prefs_podcast_library_categories_title

        return builder.setTitle(title)
            .setNeutralButton(R.string.popup_neutral_reset, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
            .setPositiveButton(R.string.popup_positive_save, null)
    }

    override fun setupRecyclerView(list: RecyclerView) {
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)
        setupDragListener(list, 0)
        adapter.submitList(presenter.getDataSet(category))
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        disposeDragListener()
    }

    override fun positiveAction() {
        presenter.setDataSet(category, adapter.getData())
        requireActivity().recreate()
        dismiss()
    }

    override fun neutralAction() {
        val defaultData = presenter.getDefaultDataSet(category)
        adapter.submitList(defaultData)
    }

}