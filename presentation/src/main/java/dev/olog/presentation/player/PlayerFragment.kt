package dev.olog.presentation.player

import android.os.Bundle
import android.view.View
import androidx.core.math.MathUtils.clamp
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.media.mediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.interfaces.slidingPanel
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.launchIn
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.playerAppearanceAmbient
import dev.olog.shared.android.utils.isMarshmallow
import dev.olog.shared.mapListItem
import kotlinx.android.synthetic.main.fragment_player_default.*
import kotlinx.android.synthetic.main.player_toolbar_default.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class PlayerFragment : BaseFragment(), IDragListener by DragListenerImpl() {

    private val viewModel by viewModels<PlayerFragmentViewModel>()

    @Inject
    internal lateinit var presenter: PlayerFragmentPresenter
    @Inject
    lateinit var navigator: Navigator

    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val playerAppearanceAmbient = requireContext().playerAppearanceAmbient
        val adapter = PlayerFragmentAdapter(
            mediaProvider = requireActivity().mediaProvider,
            navigator = navigator,
            viewModel = viewModel,
            presenter = presenter,
            dragListener = this,
            playerAppearanceAdaptiveBehavior = IPlayerAppearanceAdaptiveBehavior.get(playerAppearanceAmbient.value)
        )

        layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setHasFixedSize(true)

        setupDragListener(list, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT)

        val statusBarAlpha = if (!isMarshmallow()) 1f else 0f
        statusBar?.alpha = statusBarAlpha

        requireActivity().mediaProvider.queue
            .mapListItem { it.toDisplayableItem() }
            .map { queue ->
                if (!playerAppearanceAmbient.isMini()) {
                    val copy = queue.toMutableList()
                    if (copy.size > PlayingQueueGateway.MINI_QUEUE_SIZE - 1) {
                        copy.add(viewModel.footerLoadMore)
                    }
                    copy.add(0, viewModel.playerControls())
                    copy
                } else {
                    listOf(viewModel.playerControls())
                }
            }
            .flowOn(Dispatchers.Default)
            .onEach(adapter::submitList)
            .launchIn(this)
    }

    override fun onResume() {
        super.onResume()
        slidingPanel.addBottomSheetCallback(slidingPanelListener)
    }

    override fun onPause() {
        super.onPause()
        slidingPanel.removeBottomSheetCallback(slidingPanelListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
    }

    override fun provideLayoutId(): Int {
        val playerAppearanceAmbient = requireContext().playerAppearanceAmbient
        return when (playerAppearanceAmbient.value) {
            PlayerAppearance.FULLSCREEN -> R.layout.fragment_player_fullscreen
            PlayerAppearance.CLEAN -> R.layout.fragment_player_clean
            PlayerAppearance.MINI -> R.layout.fragment_player_mini
            PlayerAppearance.SPOTIFY -> R.layout.fragment_player_spotify
            PlayerAppearance.BIG_IMAGE -> R.layout.fragment_player_big_image
            else -> R.layout.fragment_player_default
        }
    }

    private val slidingPanelListener = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (!isMarshmallow() && slideOffset in .9f..1f) {
                val alpha = (1 - slideOffset) * 10
                statusBar?.alpha = clamp(abs(1 - alpha), 0f, 1f)
            }
            val alpha = clamp(slideOffset * 5f, 0f, 1f)
            requireView().alpha = alpha
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