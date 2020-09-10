package dev.olog.feature.queue

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.adapter.drag.DragListenerImpl
import dev.olog.feature.presentation.base.adapter.drag.IDragListener
import dev.olog.feature.presentation.base.extensions.awaitAnimationEnd
import dev.olog.feature.presentation.base.extensions.dip
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.queue.adapter.PlayingQueueFragmentAdapter
import dev.olog.navigation.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.lazyFast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@AndroidEntryPoint
internal class PlayingQueueFragment : BaseFragment(), IDragListener by DragListenerImpl() {

    companion object {
        val TAG = PlayingQueueFragment::class.java.name

        @JvmStatic
        fun newInstance(): PlayingQueueFragment {
            return PlayingQueueFragment()
        }
    }

    private val viewModel by viewModels<PlayingQueueFragmentViewModel>()

    @Inject
    internal lateinit var navigator: Navigator

    private val adapter by lazyFast {
        PlayingQueueFragmentAdapter(mediaProvider, navigator, this, viewModel)
    }

    @SuppressLint("ConcreteDispatcherIssue")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val layoutManager = OverScrollLinearLayoutManager(list)
//        list.adapter = adapter
//        list.layoutManager = layoutManager
//        list.setHasFixedSize(true)
//        fastScroller.attachRecyclerView(list)
//        fastScroller.showBubble(false)

//        setupDragListener(list, ItemTouchHelper.RIGHT)

        viewModel.observeData()
            .onEach {
                adapter.suspendSubmitList(it)
//                list.awaitAnimationEnd()
//                emptyStateText.isVisible = it.isEmpty()
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        adapter.observeData
            .take(1)
            .map { queue ->
                val idInPlaylist = viewModel.getLastIdInPlaylist()
                queue.indexOfFirst { it.idInPlaylist == idInPlaylist }
            }
            .filter { it != RecyclerView.NO_POSITION } // filter only valid position
            .flowOn(Dispatchers.Default)
            .onEach { position ->
//                layoutManager.scrollToPositionWithOffset(position, requireContext().dip(20))
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
//        more.setOnClickListener {
            // TODO restore navigation
//            navigator.toMainPopup(it, MainPopupCategory.PLAYING_QUEUE)
//        }
//        floatingWindow.setOnClickListener { navigator.toFloating() }
    }

    override fun onPause() {
        super.onPause()
//        more.setOnClickListener(null)
//        floatingWindow.setOnClickListener(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        list.adapter = null
        disposeDragListener()
    }

    override fun onCurrentPlayingChanged(mediaId: PresentationId.Track) {
        adapter.onCurrentPlayingChanged(adapter, mediaId)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}