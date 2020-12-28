package dev.olog.feature.queue

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.base.adapter.drag.DragListenerImpl
import dev.olog.feature.base.adapter.drag.IDragListener
import dev.olog.feature.base.base.BaseFragment
import dev.olog.lib.media.mediaProvider
import dev.olog.navigation.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.dip
import dev.olog.shared.android.extensions.launch
import dev.olog.shared.android.extensions.launchIn
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_playing_queue.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@AndroidEntryPoint
class PlayingQueueFragment : BaseFragment(), IDragListener by DragListenerImpl() {

    // TODO check why is using viewModels on activity
    private val viewModel by activityViewModels<PlayingQueueFragmentViewModel>()

    @Inject
    lateinit var navigator: Navigator

    private val adapter by lazyFast {
        PlayingQueueFragmentAdapter(
            mediaProvider = requireActivity().mediaProvider,
            navigator = navigator,
            dragListener = this,
            viewModel = viewModel
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setHasFixedSize(true)
        fastScroller.attachRecyclerView(list)
        fastScroller.showBubble(false)

        setupDragListener(list, ItemTouchHelper.RIGHT)

        viewModel.observeData()
            .onEach {
                adapter.submitList(it)
                emptyStateText.isVisible = it.isEmpty()
            }.launchIn(this)

        launch {
            adapter.observeData()
                .take(1)
                .map { viewModel.lastProgressive }
                .filter { it != RecyclerView.NO_POSITION } // filter only valid position
                .flowOn(Dispatchers.Default)
                .collect { position ->
                    layoutManager.scrollToPositionWithOffset(position, dip(20))
                }
        }
    }

    override fun onResume() {
        super.onResume()
        more.setOnClickListener {
//            navigator.toMainPopup(it, MediaIdCategory.PLAYING_QUEUE) // TODO create queue popup
        }
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
    }

    override fun onPause() {
        super.onPause()
        more.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
    }

    private fun startServiceOrRequestOverlayPermission() {
//        FloatingWindowHelper.startServiceOrRequestOverlayPermission(requireActivity()) TODO
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}