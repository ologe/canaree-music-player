package dev.olog.presentation.player

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.math.MathUtils.clamp
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.base.drag.DragListenerImpl
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.shared.extensions.*
import dev.olog.shared.theme.PlayerAppearance
import dev.olog.shared.theme.hasPlayerAppearance
import dev.olog.shared.utils.isMarshmallow
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_player_default.*
import kotlinx.android.synthetic.main.player_toolbar_default.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

class PlayerFragment : BaseFragment(), IDragListener by DragListenerImpl() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast {
        viewModelProvider<PlayerFragmentViewModel>(viewModelFactory)
    }
    @Inject
    lateinit var presenter: PlayerFragmentPresenter
    @Inject
    lateinit var navigator: Navigator

    @Inject lateinit var musicPrefs: MusicPreferencesGateway

    private lateinit var layoutManager: LinearLayoutManager

    private val mediaProvider by lazyFast { act as MediaProvider }

    private var lyricsDisposable: Disposable? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val hasPlayerAppearance = requireContext().hasPlayerAppearance()

        val adapter = PlayerFragmentAdapter(
            lifecycle, activity as MediaProvider,
            navigator, viewModel, presenter, musicPrefs,
            this, IPlayerAppearanceAdaptiveBehavior.get(hasPlayerAppearance.playerAppearance())
        )

        layoutManager = LinearLayoutManager(context)
        list.adapter = adapter
        list.layoutManager = layoutManager
        list.setHasFixedSize(true)
        list.isNestedScrollingEnabled = false

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
            .assertBackground()
            .flowOn(Dispatchers.Default)
            .asLiveData()
            .subscribe(viewLifecycleOwner, adapter::updateDataSet)
    }

    override fun onResume() {
        super.onResume()
        getSlidingPanel()?.addPanelSlideListener(slidingPanelListener)
    }

    override fun onPause() {
        super.onPause()
        getSlidingPanel()?.removePanelSlideListener(slidingPanelListener)
    }

    override fun onStop() {
        super.onStop()
        lyricsDisposable.unsubscribe()
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
                lyricsDisposable.unsubscribe()
                lyricsDisposable = Completable.timer(50, TimeUnit.MILLISECONDS, Schedulers.io())
                    .andThen(viewModel.showLyricsTutorialIfNeverShown())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ lyrics?.let { TutorialTapTarget.lyrics(it) } }, {})
            } else {
                lyricsDisposable.unsubscribe()
            }
        }
    }
}