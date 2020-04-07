package dev.olog.presentation.queue

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dev.olog.lib.media.MediaProvider
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.PresentationId
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.popup.main.MainPopupCategory
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.awaitAnimationEnd
import dev.olog.shared.android.extensions.dip
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_playing_queue.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
    private val viewModel by viewModels<PlayingQueueFragmentViewModel> {
        viewModelFactory
    }

    @Inject
    internal lateinit var navigator: Navigator

    private val adapter by lazyFast {
        PlayingQueueFragmentAdapter(requireActivity() as MediaProvider, navigator, this, viewModel)
    }

    @SuppressLint("ConcreteDispatcherIssue")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setHasFixedSize(true)
        fastScroller.attachRecyclerView(list)
        fastScroller.showBubble(false)

        setupDragListener(list, ItemTouchHelper.RIGHT)

        viewModel.observeData()
            .onEach {
                adapter.suspendSubmitList(it)
                list.awaitAnimationEnd()
                emptyStateText.isVisible = it.isEmpty()
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
                layoutManager.scrollToPositionWithOffset(position, requireContext().dip(20))
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
        more.setOnClickListener { navigator.toMainPopup(it, MainPopupCategory.PLAYING_QUEUE) }
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
    }

    override fun onPause() {
        super.onPause()
        more.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
        disposeDragListener()
    }

    override fun onCurrentPlayingChanged(mediaId: PresentationId.Track) {
        adapter.onCurrentPlayingChanged(adapter, mediaId)
    }

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(requireActivity())
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}