package dev.olog.presentation.fragment_mini_queue

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.presentation.utils.extension.subscribe
import kotlinx.android.synthetic.main.fragment_mini_queue.view.*
import javax.inject.Inject

class MiniQueueFragment : BaseFragment() {

    @Inject lateinit var viewModel: MiniQueueViewModel
    @Inject lateinit var adapter : MiniQueueFragmentAdapter

    private lateinit var layoutManager: LinearLayoutManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.data.subscribe(this, adapter::updateDataSet)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        super.onViewBound(view, savedInstanceState)
        layoutManager = LinearLayoutManager(context)
        view.list.layoutManager = layoutManager
        view.list.adapter = adapter

        RxRecyclerView.scrollEvents(view.list)
                .map { it.view() }
                .map { it.canScrollVertically(-1) }
                .distinctUntilChanged()
                .asLiveData()
                .subscribe(this, {
                    val toolbar = activity!!.findViewById<View>(R.id.wrapper)
                    toolbar?.isActivated = it
                })
    }

    fun smoothScrollToTop(){
        view?.list?.stopScroll()
        view?.list?.smoothScrollToPosition(0)
    }

    fun cannotScrollUp(): Boolean {
        return view?.list?.canScrollVertically(-1) == true
    }

    override fun provideLayoutId(): Int = R.layout.fragment_mini_queue
}
