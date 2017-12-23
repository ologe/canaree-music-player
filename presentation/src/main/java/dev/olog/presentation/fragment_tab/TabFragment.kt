package dev.olog.presentation.fragment_tab

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.GridLayoutManager
import android.view.View
import dagger.Lazy
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.fragment_tab.di.TabFragmentViewModelModule
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.extension.withArguments
import kotlinx.android.synthetic.main.fragment_tab.view.*
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class TabFragment : BaseFragment() {

    companion object {

        private const val TAG = "TabFragment"
        const val ARGUMENTS_SOURCE = "$TAG.argument.dataSource"

        fun newInstance(source: Int): TabFragment {
            return TabFragment().withArguments(
                    ARGUMENTS_SOURCE to source)
        }
    }

    @Inject lateinit var adapter: TabFragmentAdapter
    @Inject lateinit var viewModel: TabFragmentViewModel
    @Inject @JvmField var source: Int = 0
    private val spanSizeLookup by lazy (NONE) { TabSpanSpanSizeLookupFactory(context!!, source, adapter) }
    private lateinit var layoutManager: GridLayoutManager

    @Inject lateinit var lastAlbumsAdapter : Lazy<TabFragmentLastPlayedAlbumsAdapter>
    @Inject lateinit var lastArtistsAdapter : Lazy<TabFragmentLastPlayedArtistsAdapter>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.observeData(source)
                .subscribe(this, { list ->
                    handleEmpyStateVisibility(list.isEmpty())
                    adapter.updateDataSet(list)
                })

        when (source){
            TabViewPagerAdapter.ALBUM -> {
                viewModel.observeData(TabFragmentViewModelModule.LAST_PLAYED_ALBUM)
                        .subscribe(this, { lastAlbumsAdapter.get().updateDataSet(it) })
            }
            TabViewPagerAdapter.ARTIST -> {
                viewModel.observeData(TabFragmentViewModelModule.LAST_PLAYED_ARTIST)
                        .subscribe(this, { lastArtistsAdapter.get().updateDataSet(it) })
            }
        }
    }

    private fun handleEmpyStateVisibility(isEmpty: Boolean){
        val visibility = if (isEmpty) View.VISIBLE else View.GONE
        if (isEmpty){
            val emptyText = context!!.resources.getStringArray(R.array.tab_empty_state)
            val emptyImage = context!!.resources.obtainTypedArray(R.array.tab_empty_image)
            view!!.emptyStateText.text = emptyText[source]
            view!!.emptyStateImage.setImageResource(emptyImage.getResourceId(source, -1))
            emptyImage.recycle()
        }
        view!!.emptyStateText.visibility = visibility
        view!!.emptyStateImage.visibility = visibility
    }

    @CallSuper
    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = GridLayoutManager(context, spanSizeLookup.getSpanSize())
        layoutManager.spanSizeLookup = spanSizeLookup.get()
        view.list.layoutManager = layoutManager
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)
        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.setSectionIndexer(adapter)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_tab
}