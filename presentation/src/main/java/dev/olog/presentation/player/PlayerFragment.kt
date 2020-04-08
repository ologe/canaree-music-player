package dev.olog.presentation.player

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.core.math.MathUtils.clamp
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.lib.media.MediaProvider
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.presentation.R
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.adapter.drag.DragListenerImpl
import dev.olog.feature.presentation.base.adapter.drag.IDragListener
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.themeManager
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.core.isMarshmallow
import dev.olog.shared.lazyFast
import dev.olog.shared.coroutines.mapListItem
import kotlinx.android.synthetic.main.fragment_player_default.*
import kotlinx.android.synthetic.main.player_toolbar_default.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.math.abs

@Keep
class PlayerFragment : BaseFragment(), IDragListener by DragListenerImpl() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<PlayerFragmentViewModel> {
        viewModelFactory
    }

    @Inject
    internal lateinit var presenter: PlayerFragmentPresenter
    @Inject
    internal lateinit var navigator: Navigator

    @Inject lateinit var musicPrefs: MusicPreferencesGateway

    private lateinit var layoutManager: LinearLayoutManager

    private val mediaProvider by lazyFast { requireActivity() as MediaProvider }

    private val adapter by lazyFast {
        val playerAppearance = themeManager.playerAppearance
        PlayerFragmentAdapter(
            activity as MediaProvider,
            navigator, viewModel, presenter, musicPrefs,
            this, IPlayerAppearanceAdaptiveBehavior.get(playerAppearance)
        )
    }

    @SuppressLint("ConcreteDispatcherIssue")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setHasFixedSize(true)

        setupDragListener(list, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT)

        val statusBarAlpha = if (!isMarshmallow()) 1f else 0f
        statusBar?.alpha = statusBarAlpha

        val playerAppearance = themeManager.playerAppearance

        mediaProvider.observeQueue()
            .mapListItem { it.toDisplayableItem() }
            .map { queue ->
                if (!playerAppearance.isMini) {
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
            .onEach { adapter.submitList(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
        getSlidingPanel()?.addBottomSheetCallback(slidingPanelListener)
    }

    override fun onPause() {
        super.onPause()
        getSlidingPanel()?.removeBottomSheetCallback(slidingPanelListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
        disposeDragListener()
    }

    override fun onCurrentPlayingChanged(mediaId: PresentationId.Track) {
        adapter.onCurrentPlayingChanged(adapter, mediaId)
    }

    override fun provideLayoutId(): Int {
        return when (themeManager.playerAppearance) {
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