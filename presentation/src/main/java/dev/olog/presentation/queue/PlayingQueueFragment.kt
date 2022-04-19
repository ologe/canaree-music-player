package dev.olog.presentation.queue

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
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.navigator.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.awaitLifecycle
import dev.olog.shared.android.extensions.collectOnViewLifecycle
import dev.olog.shared.android.extensions.dip
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.lazyFast
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
    lateinit var navigator: Navigator

    private val adapter by lazyFast {
        PlayingQueueFragmentAdapter(
            mediaProvider = requireContext().findInContext(),
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

        setupDragListener(viewLifecycleOwner.lifecycleScope, list, ItemTouchHelper.RIGHT)

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
            .awaitLifecycle(this, Lifecycle.State.RESUMED)
            .collectOnViewLifecycle(this) { position ->
                layoutManager.scrollToPositionWithOffset(position, dip(20))
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
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}