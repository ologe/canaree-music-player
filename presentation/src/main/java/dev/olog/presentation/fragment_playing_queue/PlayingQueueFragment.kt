package dev.olog.presentation.fragment_playing_queue

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation._base.list.OnDataChangedListener
import dev.olog.presentation.utils.animation.CircularReveal
import dev.olog.presentation.utils.extension.subscribe
import kotlinx.android.synthetic.main.fragment_playing_queue.view.*
import kotlinx.android.synthetic.main.layout_player_toolbar.*
import org.jetbrains.anko.dip
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment() {

    companion object {
        const val TAG = "PlayingQueueFragment"
    }

    @Inject lateinit var adapter: PlayingQueueFragmentAdapter
    private lateinit var layoutManager : LinearLayoutManager
    @Inject lateinit var viewModel : PlayingQueueFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null){
            postponeEnterTransition()
            enterTransition = CircularReveal(activity!!.playingQueue)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.data.subscribe(this, adapter::updateDataSet)

        viewModel.observeCurrentSongId.subscribe(this, {
            adapter.notifyDataSetChanged()
        })
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)
        adapter.touchHelper()!!.attachToRecyclerView(view.list)

        adapter.onDataChangedListener = object : OnDataChangedListener {
            override fun onChanged() {
                adapter.onDataChangedListener = null
                val songId = viewModel.getCurrentSongId()
                val position = adapter.getItemPositionById { it ->
                    it.mediaId.leaf!!.toInt() == songId
                }
                layoutManager.scrollToPositionWithOffset(position, context!!.dip(20))
                startPostponedEnterTransition()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        view!!.back.setOnClickListener { activity!!.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        view!!.back.setOnClickListener(null)
    }

    override fun onDestroy() {
        adapter.onDataChangedListener = null
        super.onDestroy()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}