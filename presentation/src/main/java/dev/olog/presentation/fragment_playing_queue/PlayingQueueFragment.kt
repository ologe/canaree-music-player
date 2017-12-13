package dev.olog.presentation.fragment_playing_queue

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.extension.subscribe
import kotlinx.android.synthetic.main.fragment_playing_queue.view.*
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment() {

    companion object {
        const val TAG = "PlayingQueueFragment"
    }

    @Inject lateinit var adapter: PlayingQueueFragmentAdapter
    private lateinit var layoutManager : LinearLayoutManager
    @Inject lateinit var viewModel : PlayingQueueFragmentViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.data.subscribe(this, {
            println(it)
            adapter.updateDataSet(it)
        })
        viewModel.metadata.subscribe(this, {})
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue
}