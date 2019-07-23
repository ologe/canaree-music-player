package dev.olog.presentation.queue

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaIdCategory
import dev.olog.media.MediaProvider
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.extensions.*
import kotlinx.android.synthetic.main.fragment_playing_queue.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layoutManager = LinearLayoutManager(context!!)
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

        launch {
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

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}