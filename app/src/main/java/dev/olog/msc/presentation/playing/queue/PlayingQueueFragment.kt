package dev.olog.msc.presentation.playing.queue

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.adp.TouchHelperAdapterCallback
import dev.olog.msc.presentation.utils.CircularReveal
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.withArguments
import kotlinx.android.synthetic.main.fragment_playing_queue.view.*
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment() {

    companion object {
        const val TAG = "PlayingQueueFragment"
        private const val ARGUMENT_ICON_POS_X = TAG + ".argument.pos.x"
        private const val ARGUMENT_ICON_POS_Y = TAG + ".argument.pos.y"

        @JvmStatic
        fun newInstance(icon: View): PlayingQueueFragment {
            val x = (icon.x + icon.width / 2).toInt()
            val y = (icon.y + icon.height / 2).toInt()
            return PlayingQueueFragment().withArguments(
                    ARGUMENT_ICON_POS_X to x,
                    ARGUMENT_ICON_POS_Y to y
            )
        }
    }

    @Inject lateinit var viewModel : PlayingQueueFragmentViewModel
    @Inject lateinit var adapter: PlayingQueueFragmentAdapter
    private lateinit var layoutManager : LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null){
            postponeEnterTransition()
            val x = arguments!!.getInt(ARGUMENT_ICON_POS_X)
            val y = arguments!!.getInt(ARGUMENT_ICON_POS_Y)
            enterTransition = CircularReveal(x, y)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.data.subscribe(this, adapter::updateDataSet)

        viewModel.observeCurrentSongId.subscribe(this, {
//            adapter.updateCurrentPosition(it) todo
            adapter.notifyDataSetChanged()
        })
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)

        val callback = TouchHelperAdapterCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper

        adapter.setAfterDataChanged({
            adapter.setAfterDataChanged(null)
//            val songId = viewModel.getCurrentSongId() todo
//            val position = adapter.getItemPositionByPredicate {
//                it.trackNumber.toInt() == songId
//            }
//            adapter.updateCurrentPosition(position)
//            layoutManager.scrollToPositionWithOffset(position, context!!.dip(20))
            startPostponedEnterTransition()
        })
    }

    override fun onResume() {
        super.onResume()
        view!!.back.setOnClickListener { activity!!.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        view!!.back.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        adapter.setAfterDataChanged(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}