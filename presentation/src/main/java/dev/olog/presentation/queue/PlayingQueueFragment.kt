package dev.olog.presentation.queue

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaIdCategory
import dev.olog.feature.bubble.api.FeatureBubbleNavigator
import dev.olog.feature.media.api.mediaProvider
import dev.olog.platform.extension.ctx
import dev.olog.platform.extension.dip
import dev.olog.platform.extension.toggleVisibility
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.navigator.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.platform.extension.*
import dev.olog.shared.lazyFast
import dev.olog.shared.subscribe
import kotlinx.android.synthetic.main.fragment_playing_queue.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlayingQueueFragment : BaseFragment(), IDragListener by DragListenerImpl() {

    companion object {
        val TAG = PlayingQueueFragment::class.java.name

        fun newInstance(): PlayingQueueFragment {
            return PlayingQueueFragment()
        }
    }

    private val viewModel by activityViewModels<PlayingQueueFragmentViewModel>()
    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var featureBubbleNavigator: FeatureBubbleNavigator

    private val adapter by lazyFast {
        PlayingQueueFragmentAdapter(
            lifecycle = lifecycle,
            mediaProvider = mediaProvider,
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

        setupDragListener(viewLifecycleOwner, list, ItemTouchHelper.RIGHT)

        viewModel.observeData().subscribe(viewLifecycleOwner) {
            adapter.updateDataSet(it)
            emptyStateText.toggleVisibility(it.isEmpty(), true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.observeData(false)
                .take(1)
                .map {
                    val idInPlaylist = viewModel.getLastIdInPlaylist()
                    it.indexOfFirst { it.idInPlaylist == idInPlaylist }
                }
                .filter { it != RecyclerView.NO_POSITION } // filter only valid position
                .flowOn(Dispatchers.Default)
                .collect { position ->
                    layoutManager.scrollToPositionWithOffset(
                        position,
                        ctx.dip(20)
                    )
                }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
    }

    private fun startServiceOrRequestOverlayPermission() {
        featureBubbleNavigator.startServiceOrRequestOverlayPermission(requireActivity())
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}