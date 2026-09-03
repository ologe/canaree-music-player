package dev.olog.presentation.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.core.math.MathUtils.clamp
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.lifecycle.lifecycleScope
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.scrollhelper.layoutmanagers.OverScrollLinearLayoutManager
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.android.utils.isMarshmallow
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs
import dev.olog.presentation.widgets.StatusBarView

@AndroidEntryPoint
@Keep
class PlayerFragment : BaseFragment(), IDragListener by DragListenerImpl() {

    private val viewModel by viewModels<PlayerFragmentViewModel>()

    @Inject
    lateinit var navigator: Navigator

    @Inject lateinit var musicPrefs: MusicPreferencesGateway

    private lateinit var layoutManager: LinearLayoutManager

    private val mediaProvider by lazyFast { act.asType<MediaProvider>() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(provideLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val hasPlayerAppearance = requireContext().hasPlayerAppearance()

        val adapter = PlayerFragmentAdapter(
            lifecycle, activity!!.asType<MediaProvider>(),
            navigator, viewModel, musicPrefs,
            this, IPlayerAppearanceAdaptiveBehavior.get(hasPlayerAppearance.playerAppearance())
        )

        val list = view.findViewById<RecyclerView>(R.id.list)
        layoutManager = OverScrollLinearLayoutManager(list)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setHasFixedSize(true)

        setupDragListener(list, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT, viewLifecycleOwner.lifecycleScope)

        val statusBarAlpha = if (!isMarshmallow()) 1f else 0f
        view.findViewById<StatusBarView>(R.id.statusBar)?.alpha = statusBarAlpha

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
        getSlidingPanel()?.addBottomSheetCallback(slidingPanelListener)
    }

    override fun onPause() {
        super.onPause()
        getSlidingPanel()?.removeBottomSheetCallback(slidingPanelListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        view?.findViewById<RecyclerView>(R.id.list)?.adapter = null
    }

    private fun provideLayoutId(): Int {
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
                view?.findViewById<StatusBarView>(R.id.statusBar)?.alpha = clamp(abs(1 - alpha), 0f, 1f)
            }
            val alpha = clamp(slideOffset * 5f, 0f, 1f)
            view?.alpha = alpha
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                if (viewModel.showLyricsTutorialIfNeverShown()){
                    view?.findViewById<View>(R.id.lyrics)?.let { TutorialTapTarget.lyrics(it) }
                }
            }
        }
    }
}