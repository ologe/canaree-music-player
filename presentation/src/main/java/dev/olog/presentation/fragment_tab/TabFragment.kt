package dev.olog.presentation.fragment_tab

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.GridLayoutManager
import android.view.View
import dagger.Lazy
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.extension.toggleVisibility
import dev.olog.presentation.utils.extension.withArguments
import kotlinx.android.synthetic.main.fragment_tab.view.*
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class TabFragment : BaseFragment() {

    companion object {

        private const val TAG = "TabFragment"
        const val ARGUMENTS_SOURCE = "$TAG.argument.dataSource"

        fun newInstance(category: MediaIdCategory): TabFragment {
            return TabFragment().withArguments(ARGUMENTS_SOURCE to category.ordinal)
        }
    }

    @Inject lateinit var adapter: TabFragmentAdapter
    @Inject lateinit var viewModel: TabFragmentViewModel
    @Inject lateinit var category: MediaIdCategory
    private val spanSizeLookup by lazy (NONE) { TabSpanSpanSizeLookupFactory(context!!, category, adapter) }
    private lateinit var layoutManager: GridLayoutManager

    @Inject lateinit var lastAlbumsAdapter : Lazy<TabFragmentLastPlayedAlbumsAdapter>
    @Inject lateinit var lastArtistsAdapter : Lazy<TabFragmentLastPlayedArtistsAdapter>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.observeData(category)
                .subscribe(this, { list ->
                    handleEmptyStateVisibility(list.isEmpty())
                    adapter.updateDataSet(list)
                })

        when (category){
            MediaIdCategory.ALBUM -> {
                viewModel.observeData(MediaIdCategory.RECENT_ALBUMS)
                        .subscribe(this, { lastAlbumsAdapter.get().updateDataSet(it) })
            }
            MediaIdCategory.ARTIST -> {
                viewModel.observeData(MediaIdCategory.RECENT_ARTISTS)
                        .subscribe(this, { lastArtistsAdapter.get().updateDataSet(it) })
            }
        }
    }

    private fun handleEmptyStateVisibility(isEmpty: Boolean){
        view!!.emptyStateText.toggleVisibility(isEmpty)
        if (isEmpty){
            val emptyText = context!!.resources.getStringArray(R.array.tab_empty_state)
            view!!.emptyStateText.text = emptyText[category.ordinal]
        }
    }

    @CallSuper
    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = GridLayoutManager(context, spanSizeLookup.getSpanSize())
        layoutManager.spanSizeLookup = spanSizeLookup.get()
        view.list.layoutManager = layoutManager
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)
        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(true)
        view.fastScroller.setSectionIndexer(adapter)
    }


    override fun provideLayoutId(): Int = R.layout.fragment_tab
}