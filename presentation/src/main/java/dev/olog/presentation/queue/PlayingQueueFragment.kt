package dev.olog.presentation.queue

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaIdCategory
import dev.olog.media.MediaProvider
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.viewLifecycleScope
import dev.olog.presentation.databinding.FragmentPlayingQueueBinding
import dev.olog.presentation.navigator.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.viewBinding
import dev.olog.shared.lazyFast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment(R.layout.fragment_playing_queue), IDragListener by DragListenerImpl() {

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

    private val binding by viewBinding(FragmentPlayingQueueBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layoutManager = OverScrollLinearLayoutManager(binding.list)
        binding.list.adapter = adapter
        binding.list.layoutManager = layoutManager
        binding.list.setHasFixedSize(true)
        binding.fastScroller.attachRecyclerView(binding.list)
        binding.fastScroller.showBubble(false)

        setupDragListener(binding.list, ItemTouchHelper.RIGHT)

        viewModel.observeData().subscribe(viewLifecycleOwner) {
            adapter.updateDataSet(it)
            binding.emptyStateText.toggleVisibility(it.isEmpty(), true)
        }

        viewLifecycleScope.launch {
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
        binding.more.setOnClickListener { navigator.toMainPopup(it, MediaIdCategory.PLAYING_QUEUE) }
        binding.floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
    }

    override fun onPause() {
        super.onPause()
        binding.more.setOnClickListener(null)
        binding.floatingWindow.setOnClickListener(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.list.adapter = null
    }

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!)
    }


}