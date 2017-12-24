package dev.olog.presentation.fragment_playing_queue

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.AnimationUtils
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_playing_queue.view.*
import kotlinx.android.synthetic.main.layout_player_toolbar.*
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment() {

    companion object {
        const val TAG = "PlayingQueueFragment"
        private const val ANIMATION_DONE = "$TAG.ANIMATION_DONE"
    }

    @Inject lateinit var adapter: PlayingQueueFragmentAdapter
    private lateinit var layoutManager : LinearLayoutManager
    @Inject lateinit var viewModel : PlayingQueueFragmentViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.data.subscribe(this, adapter::updateDataSet)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)
        val callback = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper

        if (savedInstanceState == null){
            view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View?, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int) {
                    v?.removeOnLayoutChangeListener(this)
                    AnimationUtils.startCircularReveal(view.root, activity!!.playingQueue)
                }
            })
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ANIMATION_DONE, true)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue

    fun onBackPressed(){
        AnimationUtils.stopCircularReveal(view!!.root, activity!!.playingQueue,
                activity!!.supportFragmentManager)
    }

}