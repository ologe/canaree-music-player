package dev.olog.feature.queue

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaIdCategory
import dev.olog.media.MediaProvider
import dev.olog.feature.base.BaseFragment
import dev.olog.feature.base.drag.DragListenerImpl
import dev.olog.feature.base.drag.IDragListener
import dev.olog.feature.dialogs.FeatureDialogsNavigator
import dev.olog.feature.floating.FeatureFloatingNavigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_playing_queue.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
    lateinit var dialogsNavigator: FeatureDialogsNavigator
    @Inject
    lateinit var floatingNavigator: FeatureFloatingNavigator

    private val adapter by lazyFast {
        PlayingQueueFragmentAdapter(
            lifecycle = lifecycle,
            mediaProvider = act as MediaProvider,
            onItemLongClick = { mediaId, view -> dialogsNavigator.toDialog(requireActivity(), mediaId, view) },
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

        viewModel.observeData().subscribe(viewLifecycleOwner) {
            adapter.updateDataSet(it)
            emptyStateText.toggleVisibility(it.isEmpty(), true)
        }

        adapter.observeData(false)
            .take(1)
            .map {
                val idInPlaylist = viewModel.getLastIdInPlaylist()
                it.indexOfFirst { it.idInPlaylist == idInPlaylist }
            }
            .filter { it != RecyclerView.NO_POSITION } // filter only valid position
            .flowOn(Dispatchers.Default)
            .collectOnLifecycle(this) { position ->
                layoutManager.scrollToPositionWithOffset(
                    position,
                    ctx.dip(20)
                )
            }
    }

    override fun onResume() {
        super.onResume()
        more.setOnClickListener { dialogsNavigator.toMainPopup(requireActivity(), it, MediaIdCategory.PLAYING_QUEUE) }
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
        floatingNavigator.startService(requireActivity())
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}