package dev.olog.feature.player.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.math.MathUtils.clamp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.feature.base.adapter.drag.DragListenerImpl
import dev.olog.feature.base.adapter.drag.IDragListener
import dev.olog.feature.player.PlayerTutorial
import dev.olog.feature.player.R
import dev.olog.feature.player.volume.PlayerVolumeFragment
import dev.olog.lib.media.mediaProvider
import dev.olog.navigation.Navigator
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.launchIn
import dev.olog.shared.android.extensions.locationInWindow
import dev.olog.shared.android.slidingPanel
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.playerAppearanceAmbient
import dev.olog.shared.android.utils.isMarshmallow
import dev.olog.shared.mapListItem
import dev.olog.shared.widgets.StatusBarView
import kotlinx.android.synthetic.main.fragment_player_default.*
import kotlinx.android.synthetic.main.player_toolbar_default.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class PlayerFragment : Fragment(), IDragListener by DragListenerImpl() {

    private val viewModel by viewModels<PlayerFragmentViewModel>()

    @Inject
    internal lateinit var presenter: PlayerFragmentPresenter
    @Inject
    lateinit var navigator: Navigator

    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val playerAppearanceAmbient = requireContext().playerAppearanceAmbient
        val layoutId = when (playerAppearanceAmbient.value) {
            PlayerAppearance.FULLSCREEN -> R.layout.fragment_player_fullscreen
            PlayerAppearance.CLEAN -> R.layout.fragment_player_clean
            PlayerAppearance.MINI -> R.layout.fragment_player_mini
            PlayerAppearance.SPOTIFY -> R.layout.fragment_player_spotify
            PlayerAppearance.BIG_IMAGE -> R.layout.fragment_player_big_image
            else -> R.layout.fragment_player_default
        }
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val playerAppearanceAmbient = requireContext().playerAppearanceAmbient
        val adapter = PlayerFragmentAdapter(
            mediaProvider = requireActivity().mediaProvider,
            navigator = navigator,
            viewModel = viewModel,
            presenter = presenter,
            dragListener = this,
            playerAppearanceAdaptiveBehavior = IPlayerAppearanceAdaptiveBehavior.get(
                playerAppearanceAmbient.value
            ),
            toPlayerVolume = this::toPlayerVolume
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
            .map(this::adjustQueue)
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

    private fun adjustQueue(list: List<PlayerFragmentModel>): List<PlayerFragmentModel> {
        val playerAppearanceAmbient = requireContext().playerAppearanceAmbient
        if (playerAppearanceAmbient.isMini()) {
            return listOf(viewModel.playerControls())
        }
        return buildList {
            add(viewModel.playerControls())
            addAll(list)
            if (list.size > PlayingQueueGateway.MINI_QUEUE_SIZE - 1) {
                add(PlayerFragmentModel.LoadMoreFooter)
            }
        }
    }

    private fun toPlayerVolume(view: View) {
        val location = view.locationInWindow
        val yLocation = (location[1] - StatusBarView.viewHeight).toFloat()

        val fragment = PlayerVolumeFragment.newInstance(yPosition = yLocation)
        requireActivity().supportFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            add(android.R.id.content, fragment, null)
            addToBackStack(null)
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
                    lyrics?.let(PlayerTutorial::lyrics)
                }
            }
        }
    }
}