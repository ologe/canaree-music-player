package dev.olog.presentation.queue

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import dev.olog.core.MediaIdCategory
import dev.olog.media.MediaProvider
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.extensions.*
import kotlinx.android.synthetic.main.fragment_playing_queue.*
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment(), IDragListener by DragListenerImpl() {

    companion object {
        val TAG = PlayingQueueFragment::class.java.name

        @JvmStatic
        fun newInstance(): PlayingQueueFragment {
            return PlayingQueueFragment()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast {
        act.viewModelProvider<PlayingQueueFragmentViewModel>(viewModelFactory)
    }
    @Inject
    lateinit var navigator: Navigator

    private val adapter by lazyFast {
        PlayingQueueFragmentAdapter(
            lifecycle,
            act as MediaProvider,
            navigator,
            this,
            viewModel
        )
    }

    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setHasFixedSize(true)
        fastScroller.attachRecyclerView(list)
        fastScroller.showBubble(false)

        setupDragListener(list, ItemTouchHelper.RIGHT)

        viewModel.observeData().subscribe(viewLifecycleOwner) {
            adapter.updateDataSet(it)
            emptyStateText.toggleVisibility(it.isEmpty(), true)
        }

//        launch {
//            adapter.observeData(false)
//                .filter { it.isNotEmpty() }
//                .take(1)
//                .collect {
//                    layoutManager.scrollToPositionWithOffset(
//                        viewModel.getCurrentPosition(),
//                        ctx.dip(20)
//                    )
//                }
//        }
    }

    override fun onResume() {
        super.onResume()
        more.setOnClickListener { navigator.toMainPopup(it, MediaIdCategory.PLAYING_QUEUE) }
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
    }

    override fun onPause() {
        super.onPause()
        more.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
    }

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}