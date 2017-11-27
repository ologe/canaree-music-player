package dev.olog.presentation.fragment_queue

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.subscribe
import kotlinx.android.synthetic.main.fragment_player_queue.view.*
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment() {

    @Inject lateinit var viewModel: PlayingQueueViewModel
    @Inject lateinit var adapter : PlayingQueueAdapter

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
