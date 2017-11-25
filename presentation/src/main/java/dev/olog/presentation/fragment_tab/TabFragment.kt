package dev.olog.presentation.fragment_tab

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.subscribe
import dev.olog.presentation.utils.withArguments
import kotlinx.android.synthetic.main.fragment_tab.view.*
import javax.inject.Inject

class TabFragment : BaseFragment() {

    companion object {

        private const val TAG = "TabFragment"
        const val ARGUMENTS_SOURCE = "$TAG.argument.dataSource"

        fun newInstance(source: Int): TabFragment {
            return TabFragment().withArguments(
                    ARGUMENTS_SOURCE to source)
        }
    }

    @Inject lateinit var adapter: TabAdapter
    @Inject lateinit var viewModel: TabFragmentViewModel
    @Inject @JvmField var source: Int = 0
    @Inject lateinit var tabSpanSizeLookup: TabSpanSizeLookup
    @Inject lateinit var viewPool: RecyclerView.RecycledViewPool
    private lateinit var layoutManager: GridLayoutManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.observeData(source)
                .subscribe(this, adapter::updateDataSet)
    }

    @CallSuper
    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = GridLayoutManager(context, TabSpanSizeLookup.SPAN_COUNT)
        layoutManager.spanSizeLookup = tabSpanSizeLookup
        view.list.layoutManager = layoutManager
        view.list.adapter = adapter
        view.list.recycledViewPool = viewPool
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }
}