package dev.olog.presentation.fragment_mini_queue

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.extension.asLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_mini_queue.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MiniQueueFragment : BaseFragment() {

    @Inject lateinit var viewModel: MiniQueueViewModel
    @Inject lateinit var adapter : MiniQueueFragmentAdapter

    private lateinit var layoutManager: LinearLayoutManager

    private var disposable : Disposable? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.data.subscribe(this, adapter::updateDataSet)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        super.onViewBound(view, savedInstanceState)
        layoutManager = LinearLayoutManager(context)
        view.list.layoutManager = layoutManager
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)
        adapter.touchHelper()!!.attachToRecyclerView(view.list)

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

    override fun onResume() {
        super.onResume()
        activity!!.innerPanel.addPanelSlideListener(innerPanelSlideListener)
    }

    override fun onPause() {
        super.onPause()
        activity!!.innerPanel.removePanelSlideListener(innerPanelSlideListener)
    }

    override fun onStop() {
        super.onStop()
        disposable.unsubscribe()
    }

    private val innerPanelSlideListener = object : SlidingUpPanelLayout.SimplePanelSlideListener() {
        override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
            if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                disposable.unsubscribe()
                disposable = Observable.timer(500, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ smoothScrollToTop() }, Throwable::printStackTrace)
            }
        }
    }

    private fun smoothScrollToTop(){
        view?.list?.stopScroll()
        layoutManager.scrollToPositionWithOffset(0, 0)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_mini_queue
}
