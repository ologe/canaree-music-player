package dev.olog.feature.queue

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaIdCategory
import dev.olog.feature.bubble.api.FeatureBubbleNavigator
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.main.api.FeatureMainPopupNavigator
import dev.olog.feature.media.api.MediaProvider
import dev.olog.feature.queue.databinding.FragmentPlayingQueueBinding
import dev.olog.platform.adapter.drag.DragListenerImpl
import dev.olog.platform.adapter.drag.IDragListener
import dev.olog.platform.navigation.FragmentTagFactory
import dev.olog.platform.viewBinding
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.extension.collectOnViewLifecycle
import dev.olog.shared.extension.dip
import dev.olog.shared.extension.findInContext
import dev.olog.shared.extension.lazyFast
import dev.olog.ui.adapter.drag.CircularRevealAnimationController
import javax.inject.Inject

@AndroidEntryPoint
class PlayingQueueFragment : Fragment(R.layout.fragment_playing_queue),
    IDragListener by DragListenerImpl() {

    companion object {
        val TAG = FragmentTagFactory.create(PlayingQueueFragment::class)
    }

    private val viewModel by activityViewModels<PlayingQueueFragmentViewModel>()
    private val binding by viewBinding(FragmentPlayingQueueBinding::bind)

    @Inject
    lateinit var featureMainNavigator: FeatureMainNavigator
    @Inject
    lateinit var featureMainPopupNavigator: FeatureMainPopupNavigator
    @Inject
    lateinit var featureBubbleNavigator: FeatureBubbleNavigator

    private val mediaProvider: MediaProvider
        get() = requireActivity().findInContext()

    private val adapter by lazyFast {
        PlayingQueueFragmentAdapter(
            dragListener = this,
            onItemClick = { mediaProvider.skipToQueueItem(it.idInPlaylist) },
            onItemLongClick = { view, mediaId ->
                featureMainPopupNavigator.toItemDialog(view, mediaId)
            },
            onItemMoved = { from, to ->
                mediaProvider.swap(from, to)
                viewModel.recordSwap(from, to)
            },
            onItemClear = { viewModel.applySwap() },
            onSwipeRight = { mediaProvider.remove(it) },
            afterSwipeRight = { viewModel.recalculatePositionsAfterRemove(it) }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        val layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setHasFixedSize(true)
        fastScroller.attachRecyclerView(list)
        fastScroller.showBubble(false)

        setupDragListener(
            scope = viewLifecycleOwner.lifecycleScope,
            list = list,
            direction = ItemTouchHelper.RIGHT,
            animation = CircularRevealAnimationController(),
        )

        viewModel.data
            .collectOnViewLifecycle(this@PlayingQueueFragment) {
                adapter.submitList(it)
                emptyStateText.isVisible = it.isEmpty()
            }

        scrollToCurrentItem(layoutManager)

        more.setOnClickListener { featureMainNavigator.toMainPopup(requireActivity(), it, MediaIdCategory.PLAYING_QUEUE) }
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
    }

    private fun scrollToCurrentItem(layoutManager: LinearLayoutManager) {
        viewModel.initialItemFlow
            .collectOnViewLifecycle(this, Lifecycle.State.RESUMED) { position ->
                layoutManager.scrollToPositionWithOffset(position, dip(20))
            }
    }

    private fun startServiceOrRequestOverlayPermission() {
        featureBubbleNavigator.startServiceOrRequestOverlayPermission(requireActivity())
    }


}