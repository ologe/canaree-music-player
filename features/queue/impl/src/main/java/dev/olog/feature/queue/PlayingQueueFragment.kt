package dev.olog.feature.queue

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaIdCategory
import dev.olog.feature.bubble.api.FeatureBubbleNavigator
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.main.api.FeatureMainPopupNavigator
import dev.olog.platform.adapter.drag.DragListenerImpl
import dev.olog.platform.adapter.drag.IDragListener
import dev.olog.platform.fragment.BaseFragment
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.extension.collectOnViewLifecycle
import dev.olog.shared.extension.dip
import dev.olog.shared.extension.findInContext
import dev.olog.shared.extension.lazyFast
import dev.olog.ui.adapter.drag.CircularRevealAnimationController
import kotlinx.android.synthetic.main.fragment_playing_queue.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

@AndroidEntryPoint
class PlayingQueueFragment : BaseFragment(), IDragListener by DragListenerImpl() {

    companion object {
        val TAG = PlayingQueueFragment::class.java.name

        @JvmStatic
        fun newInstance(): PlayingQueueFragment {
            return PlayingQueueFragment()
        }
    }

    private val viewModel by activityViewModels<PlayingQueueFragmentViewModel>()

    @Inject
    lateinit var featureMainNavigator: FeatureMainNavigator
    @Inject
    lateinit var featureMainPopupNavigator: FeatureMainPopupNavigator
    @Inject
    lateinit var featureBubbleNavigator: FeatureBubbleNavigator

    private val adapter by lazyFast {
        PlayingQueueFragmentAdapter(
            mediaProvider = requireContext().findInContext(),
            dragListener = this,
            viewModel = viewModel,
            onItemLongClick = { view, mediaId ->
                featureMainPopupNavigator.toItemDialog(view, mediaId)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

        viewModel.observeData()
            .collectOnViewLifecycle(this) {
                adapter.submitList(it)
                emptyStateText.isVisible = it.isEmpty()
            }
        viewModel.observeData()
            .take(1)
            .map {
                val idInPlaylist = viewModel.getLastIdInPlaylist()
                it.indexOfFirst { it.idInPlaylist == idInPlaylist }
            }
            .filter { it != RecyclerView.NO_POSITION } // filter only valid position
            .flowOn(Dispatchers.Default)
            .collectOnViewLifecycle(this, Lifecycle.State.RESUMED) { position ->
                layoutManager.scrollToPositionWithOffset(position, dip(20))
            }
    }

    override fun onResume() {
        super.onResume()
        more.setOnClickListener { featureMainNavigator.toMainPopup(requireActivity(), it, MediaIdCategory.PLAYING_QUEUE) }
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
        featureBubbleNavigator.startServiceOrRequestOverlayPermission(activity!!)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}