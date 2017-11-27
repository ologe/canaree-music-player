package dev.olog.presentation.fragment_queue

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.domain.interactor.tab.GetAllSongsUseCase
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.model.toDisplayableItem
import dev.olog.presentation.utils.asLiveData
import dev.olog.presentation.utils.subscribe
import io.reactivex.rxkotlin.toFlowable
import kotlinx.android.synthetic.main.fragment_player_queue.view.*
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment() {

    @Inject lateinit var useCase : GetAllSongsUseCase
    private val adapter by lazy { PlayingQueueAdapter(lifecycle) }

    private lateinit var layoutManager: LinearLayoutManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        useCase.execute()
                .flatMapSingle { it.toFlowable().map { it.toDisplayableItem() }.toList() }
                .asLiveData()
                .subscribe(this, adapter::updateDataSet)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        super.onViewBound(view, savedInstanceState)
        layoutManager = LinearLayoutManager(context)
        view.list.layoutManager = layoutManager
        view.list.adapter = adapter
        RxRecyclerView.scrollEvents(view.list)
                .map { view.list.canScrollVertically(-1) }
                .distinctUntilChanged()
                .asLiveData()
                .subscribe(this, {
                    val toolbar = activity!!.findViewById<View>(R.id.playingQueueRoot)
                    toolbar.isActivated = !it
                })
    }

    override fun onResume() {
        super.onResume()
        activity!!.findViewById<SlidingUpPanelLayout>(R.id.innerPanel)
                .setScrollableView(view!!.list)
    }


    override fun provideView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_player_queue, container, false)
    }
}
