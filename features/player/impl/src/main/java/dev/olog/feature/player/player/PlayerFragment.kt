package dev.olog.feature.player.player

import android.os.Bundle
import android.view.View
import androidx.core.math.MathUtils.clamp
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.feature.base.BaseFragment
import dev.olog.feature.base.drag.DragListenerImpl
import dev.olog.feature.base.drag.IDragListener
import dev.olog.feature.base.slidingPanel
import dev.olog.feature.dialogs.FeatureDialogsNavigator
import dev.olog.feature.offline.lyrics.FeatureOfflineLyricsNavigator
import dev.olog.feature.player.R
import dev.olog.media.mediaProvider
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.extensions.asLiveData
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.android.utils.isMarshmallow
import dev.olog.shared.mapListItem
import dev.olog.shared.widgets.TutorialTapTarget
import kotlinx.android.synthetic.main.fragment_player_default.*
import kotlinx.android.synthetic.main.player_toolbar_default.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class PlayerFragment : BaseFragment(), IDragListener by DragListenerImpl() {

    private val viewModel by viewModels<PlayerFragmentViewModel>()

    @Inject
    internal lateinit var presenter: PlayerFragmentPresenter

    @Inject
    lateinit var musicPrefs: MusicPreferencesGateway
    @Inject
    lateinit var offlineLyricsNavigator: FeatureOfflineLyricsNavigator
    @Inject
    lateinit var dialogNavigator: FeatureDialogsNavigator


    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val hasPlayerAppearance = requireContext().hasPlayerAppearance()

        val adapter = PlayerFragmentAdapter(
            lifecycle = lifecycle,
            mediaProvider = requireActivity().findInContext(),
            viewModel = viewModel,
            presenter = presenter,
            musicPrefs = musicPrefs,
            dragListener = this,
            playerAppearanceAdaptiveBehavior = IPlayerAppearanceAdaptiveBehavior.get(hasPlayerAppearance.playerAppearance()),
            goToOfflineLyrics = { offlineLyricsNavigator.toOfflineLyrics(requireActivity()) },
            onItemLongClick = { mediaId, v -> dialogNavigator.toDialog(requireActivity(), mediaId, v) }
        )

        layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setHasFixedSize(true)

        setupDragListener(list, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT)

        val statusBarAlpha = if (!isMarshmallow()) 1f else 0f
        statusBar?.alpha = statusBarAlpha

        mediaProvider.observeQueue()
            .mapListItem { it.toDisplayableItem() }
            .map { queue ->
                if (!hasPlayerAppearance.isMini()) {
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
            .asLiveData()
            .subscribe(viewLifecycleOwner, adapter::updateDataSet)
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
        val appearance = requireContext().hasPlayerAppearance()
        return when (appearance.playerAppearance()) {
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