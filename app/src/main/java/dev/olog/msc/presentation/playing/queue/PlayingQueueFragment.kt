package dev.olog.msc.presentation.playing.queue

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.adapter.TouchHelperAdapterCallback
import dev.olog.msc.presentation.utils.animation.CircularReveal
import dev.olog.msc.presentation.utils.animation.HasSafeTransition
import dev.olog.msc.presentation.utils.animation.SafeTransition
import dev.olog.msc.utils.k.extension.*
import kotlinx.android.synthetic.main.fragment_playing_queue.*
import kotlinx.android.synthetic.main.fragment_playing_queue.view.*
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment(), HasSafeTransition {

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
    @Inject lateinit var safeTransition: SafeTransition
    private lateinit var layoutManager : LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null){
            val x = arguments!!.getInt(ARGUMENT_ICON_POS_X)
            val y = arguments!!.getInt(ARGUMENT_ICON_POS_Y)
            safeTransition.execute(this, CircularReveal(x, y))
        } else {
            safeTransition.isAnimating = false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listenToFirstRealEmission {
            val songId = viewModel.getCurrentSongId()
            adapter.updateCurrentPosition(songId)
            layoutManager.scrollToPositionWithOffset(adapter.currentPosition, ctx.dip(20))
        }

        viewModel.data.subscribe(this, adapter::updateDataSet)

        viewModel.observeCurrentSongId
                .subscribe(this, adapter::updateCurrentPosition)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)
        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(false)

        val callback = TouchHelperAdapterCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { act.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun isAnimating(): Boolean = safeTransition.isAnimating

    private fun listenToFirstRealEmission(func: () -> Unit){
        var counter = -1
        adapter.setAfterDataChanged({
            counter++
            if (counter == 1){
                adapter.setAfterDataChanged(null)
                func()
            }

        }, false)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}