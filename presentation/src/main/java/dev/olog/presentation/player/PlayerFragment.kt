package dev.olog.presentation.player

import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.core.math.MathUtils.clamp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.getSlidingPanel
import dev.olog.presentation.base.viewLifecycleScope
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.TextUtils
import dev.olog.shared.android.extensions.act
import androidx.lifecycle.asLiveData
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.android.utils.isMarshmallow
import dev.olog.shared.lazyFast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.math.abs

@Keep
@AndroidEntryPoint
class PlayerFragment : Fragment(R.layout.fragment_player), IDragListener by DragListenerImpl() {

    private val viewModel by viewModels<PlayerFragmentViewModel>()
    @Inject
    lateinit var navigator: Navigator

    private lateinit var layoutManager: LinearLayoutManager

    private val mediaProvider by lazyFast { act.findInContext<MediaProvider>() }

    private lateinit var list: RecyclerView
    private var statusBar: View? = null
    private var lyrics: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list = view.findViewById(R.id.list)
        statusBar = view.findViewById(R.id.statusBar)
        lyrics = view.findViewById(R.id.lyrics)

        val hasPlayerAppearance = requireContext().hasPlayerAppearance()

        val adapter = PlayerFragmentAdapter(
            mediaProvider = requireActivity().findInContext<MediaProvider>(),
            navigator = navigator,
            viewModel = viewModel,
            dragListener = this,
        )

        layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setHasFixedSize(true)

        setupDragListener(viewLifecycleScope, list, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT)

        val statusBarAlpha = if (!isMarshmallow()) 1f else 0f
        statusBar?.alpha = statusBarAlpha

        combine(
            mediaProvider.observeQueue().filterNotNull(),
            hasPlayerAppearance.observePlayerAppearance()
        ) { queue, appearance ->
            if (appearance == PlayerAppearance.MINI) {
                listOf(PlayerItem.Player)
            } else {
                buildList<PlayerItem> {
                    add(PlayerItem.Player)
                    this += queue.map {
                        PlayerItem.Song(
                            mediaId = it.mediaId,
                            title = it.title,
                            subtitle = TextUtils.subtitle(it.artist, ""),
                            idInPlaylist = it.idInPlaylist
                        )
                    }
                    if (queue.size > PlayingQueueGateway.MINI_QUEUE_SIZE - 1) {
                        add(PlayerItem.LoadMore)
                    }
                }
            }
        }
            .flowOn(Dispatchers.Default)
            .asLiveData()
            .subscribe(viewLifecycleOwner, adapter::submitList)
    }

    override fun onResume() {
        super.onResume()
        getSlidingPanel()?.addPanelSlideListener(slidingPanelListener)
    }

    override fun onPause() {
        super.onPause()
        getSlidingPanel()?.removePanelSlideListener(slidingPanelListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
    }

    private val slidingPanelListener = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (!isMarshmallow() && slideOffset in .9f..1f) {
                val alpha = (1 - slideOffset) * 10
                statusBar?.alpha = clamp(abs(1 - alpha), 0f, 1f)
            }
            val alpha = clamp(slideOffset * 5f, 0f, 1f)
            view?.alpha = alpha
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                if (viewModel.showLyricsTutorialIfNeverShown()){
                    lyrics?.let { TutorialTapTarget.lyrics(it) }
                }
            }
        }
    }
}