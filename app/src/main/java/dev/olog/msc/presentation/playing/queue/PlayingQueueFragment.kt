package dev.olog.msc.presentation.playing.queue

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import dev.olog.msc.R
import dev.olog.msc.catchNothing
import dev.olog.msc.floating.window.service.FloatingWindowHelper
import dev.olog.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.adapter.drag.TouchHelperAdapterCallback
import dev.olog.presentation.navigator.Navigator
import dev.olog.core.MediaIdCategory
import dev.olog.shared.*
import dev.olog.shared.extensions.subscribe
import kotlinx.android.synthetic.main.fragment_playing_queue.*
import kotlinx.android.synthetic.main.fragment_playing_queue.view.*
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment() {

    companion object {
        val TAG = PlayingQueueFragment::class.java.name

        @JvmStatic
        fun newInstance(): PlayingQueueFragment {
            return PlayingQueueFragment()
        }
    }

    @Inject lateinit var viewModelFactory : ViewModelProvider.Factory
    @Inject lateinit var adapter: PlayingQueueFragmentAdapter
    @Inject lateinit var navigator: Navigator
    private lateinit var layoutManager : androidx.recyclerview.widget.LinearLayoutManager

    private val viewModel by lazyFast {
        act.viewModelProvider<PlayingQueueFragmentViewModel>(
            viewModelFactory
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter.onFirstEmission {
            layoutManager.scrollToPositionWithOffset(viewModel.getCurrentPosition(), ctx.dip(20))
        }
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)
        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(false)

        val callback = TouchHelperAdapterCallback(adapter, ItemTouchHelper.RIGHT)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper

        viewModel.data.subscribe(viewLifecycleOwner) {
            adapter.updateDataSet(it)
            view.emptyStateText.toggleVisibility(it.isEmpty(), true)
        }
    }

    override fun onResume() {
        super.onResume()
        more.setOnClickListener { catchNothing { navigator.toMainPopup(it, MediaIdCategory.PLAYING_QUEUE) } }
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