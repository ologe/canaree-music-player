package dev.olog.msc.presentation.library.tab

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.GridLayoutManager
import android.view.View
import dagger.Lazy
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.toggleVisibility
import dev.olog.msc.utils.k.extension.withArguments
import kotlinx.android.synthetic.main.fragment_tab.view.*
import javax.inject.Inject
import javax.inject.Provider

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
    @Inject lateinit var lastAlbumsAdapter : Lazy<TabFragmentLastPlayedAlbumsAdapter>
    @Inject lateinit var lastArtistsAdapter : Lazy<TabFragmentLastPlayedArtistsAdapter>
    @Inject lateinit var layoutManager: Provider<GridLayoutManager>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.observeData(category)
                .subscribe(this, { list ->
                    handleEmptyStateVisibility(list.isEmpty())
                    adapter.updateDataSet(list)
                })

        when (category){
            MediaIdCategory.ALBUMS -> {
                viewModel.observeData(MediaIdCategory.RECENT_ALBUMS)
                        .subscribe(this, { lastAlbumsAdapter.get().updateDataSet(it) })
            }
            MediaIdCategory.ARTISTS -> {
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
        view.list.layoutManager = layoutManager.get()
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)

        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(true)
        view.fastScroller.setSectionIndexer(adapter)
    }


    override fun provideLayoutId(): Int = R.layout.fragment_tab
}