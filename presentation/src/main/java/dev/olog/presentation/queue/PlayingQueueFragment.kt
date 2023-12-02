package dev.olog.presentation.queue

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.R
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.databinding.FragmentPlayingQueueBinding
import dev.olog.presentation.navigator.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.ctx
import dev.olog.shared.android.extensions.dip
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.android.extensions.viewBinding
import dev.olog.shared.android.extensions.viewLifecycleScope
import dev.olog.shared.lazyFast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlayingQueueFragment : Fragment(R.layout.fragment_playing_queue), IDragListener by DragListenerImpl() {

    companion object {
        val TAG = PlayingQueueFragment::class.java.name

        @JvmStatic
        fun newInstance(): PlayingQueueFragment {
            return PlayingQueueFragment()
        }
    }

    private val binding by viewBinding(FragmentPlayingQueueBinding::bind) { binding ->
        binding.list.adapter = null
    }
    private val viewModel by activityViewModels<PlayingQueueFragmentViewModel>()
    @Inject
    lateinit var navigator: Navigator

    private val adapter by lazyFast {
        PlayingQueueFragmentAdapter(
            mediaProvider = act.findInContext(),
            navigator = navigator,
            dragListener = this,
            viewModel = viewModel
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layoutManager = OverScrollLinearLayoutManager(binding.list)
        binding.list.adapter = adapter
        binding.list.layoutManager = layoutManager
        binding.list.setHasFixedSize(true)
        binding.fastScroller.attachRecyclerView(binding.list)
        binding.fastScroller.showBubble(false)

        setupDragListener(binding.list, ItemTouchHelper.RIGHT)

        viewModel.observeData().subscribe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.emptyStateText.toggleVisibility(it.isEmpty(), true)
        }

        viewLifecycleScope.launch {
            adapter.observeData()
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
        binding.more.setOnClickListener { navigator.toMainPopup(it, MediaIdCategory.PLAYING_QUEUE) }
        binding.floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
    }

    override fun onPause() {
        super.onPause()
        binding.more.setOnClickListener(null)
        binding.floatingWindow.setOnClickListener(null)
    }

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(requireActivity())
    }


}