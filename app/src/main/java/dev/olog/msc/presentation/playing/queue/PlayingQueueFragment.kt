package dev.olog.msc.presentation.playing.queue

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.catchNothing
import dev.olog.msc.floating.window.service.FloatingWindowHelper
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.adapter.drag.TouchHelperAdapterCallback
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.utils.lazyFast
import dev.olog.msc.presentation.viewModelProvider
import dev.olog.msc.utils.k.extension.ctx
import dev.olog.msc.utils.k.extension.dip
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.toggleVisibility
import kotlinx.android.synthetic.main.fragment_playing_queue.*
import kotlinx.android.synthetic.main.fragment_playing_queue.view.*
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment() {

    companion object {
        const val TAG = "PlayingQueueFragment"

        @JvmStatic
        fun newInstance(): PlayingQueueFragment {
            return PlayingQueueFragment()
        }
    }

    @Inject lateinit var viewModelFactory : ViewModelProvider.Factory
    @Inject lateinit var adapter: PlayingQueueFragmentAdapter
    @Inject lateinit var navigator: Navigator
    private lateinit var layoutManager : LinearLayoutManager

    private val viewModel by lazyFast { viewModelProvider<PlayingQueueFragmentViewModel>(viewModelFactory) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter.onFirstEmission {
            val songId = viewModel.getCurrentSongId()
            adapter.updateCurrentPosition(songId)
            layoutManager.scrollToPositionWithOffset(adapter.currentPosition, ctx.dip(20))
        }
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)
        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(false)

        val callback = TouchHelperAdapterCallback(adapter, ItemTouchHelper.RIGHT)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper

        viewModel.observeCurrentSongId
                .subscribe(viewLifecycleOwner, adapter::updateCurrentPosition)

        viewModel.data.subscribe(viewLifecycleOwner) {
            adapter.updateDataSet(it)
            view.emptyStateText.toggleVisibility(it.isEmpty(), true)
        }
    }

    override fun onResume() {
        super.onResume()
        more.setOnClickListener { catchNothing { navigator.toMainPopup(it, null) } }
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
    }

    override fun onPause() {
        super.onPause()
        more.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
    }

    private fun startServiceOrRequestOverlayPermission(){
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}