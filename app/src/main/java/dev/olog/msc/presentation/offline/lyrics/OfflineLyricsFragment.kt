package dev.olog.msc.presentation.offline.lyrics

import android.content.Intent
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import dev.olog.media.*
import dev.olog.msc.R
import dev.olog.presentation.AppConstants
import dev.olog.msc.offline.lyrics.EditLyricsDialog
import dev.olog.msc.offline.lyrics.NoScrollTouchListener
import dev.olog.msc.offline.lyrics.OfflineLyricsSyncAdjustementDialog
import dev.olog.presentation.interfaces.DrawsOnTop
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.msc.presentation.widget.animateBackgroundColor
import dev.olog.msc.presentation.widget.animateTextColor
import dev.olog.msc.utils.k.extension.*
import dev.olog.shared.extensions.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_offline_lyrics.*
import kotlinx.android.synthetic.main.fragment_offline_lyrics.view.*
import saschpe.android.customtabs.CustomTabsHelper
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OfflineLyricsFragment : BaseFragment(), DrawsOnTop {

    companion object {
        const val TAG = "OfflineLyricsFragment"

        @JvmStatic
        fun newInstance(): OfflineLyricsFragment {
            return OfflineLyricsFragment()
        }
    }

    @Inject lateinit var presenter: OfflineLyricsFragmentPresenter
    private var tutorialDisposable: Disposable? = null
    private var updateDisposable : Disposable? = null

    private val mediaProvider by lazy { activity as MediaProvider }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        super.onViewBound(view, savedInstanceState)
        postponeEnterTransition()
        view.image.post { startPostponedEnterTransition() }

        tutorialDisposable = presenter.showAddLyricsIfNeverShown()
                .subscribe({ TutorialTapTarget.addLyrics(view.search, view.edit, view.sync) }, {})

        mediaProvider.observeMetadata()
                .subscribe(viewLifecycleOwner) {
                    presenter.updateCurrentTrackId(it.getId())
                    presenter.updateCurrentMetadata(it.getTitle().toString(), it.getArtist().toString())
                    image.loadImage(it)
                    header.text = it.getTitle()
                    subHeader.text = it.getArtist()
                    seekBar.max = it.getDuration().toInt()
                }

        presenter.observeLyrics()
                .map { presenter.transformLyrics(ctx, seekBar.progress, it) }
                .map { text.precomputeText(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .asLiveData()
                .subscribe(viewLifecycleOwner) {
                    emptyState.toggleVisibility(it.isEmpty(), true)
                    text.text = it
                }

        mediaProvider.observePlaybackState()
                .filter { it.state == PlaybackState.STATE_PLAYING || it.state == PlaybackState.STATE_PAUSED }
                .subscribe(viewLifecycleOwner) {
                    val isPlaying = it.state == PlaybackState.STATE_PLAYING
                    seekBar.progress = it.position.toInt()
                    handleSeekBarState(isPlaying, it.playbackSpeed)
                }

        view.image.observePaletteColors()
                .map { it.accent }
                .observeOn(AndroidSchedulers.mainThread())
                .asLiveData()
                .subscribe(viewLifecycleOwner) { accent ->
                    subHeader.animateTextColor(accent)
                    edit.animateBackgroundColor(accent)
                }
    }

    override fun onStart() {
        super.onStart()
        blurLayout.startBlur()
    }

    override fun onResume() {
        super.onResume()
        edit.setOnClickListener {
            EditLyricsDialog.show(act, presenter.getOriginalLyrics()) { newLyrics ->
                presenter.updateLyrics(newLyrics)
            }
        }
        back.setOnClickListener { act.onBackPressed() }
        search.setOnClickListener { searchLyrics() }
        act.window.removeLightStatusBar()

        fakeNext.setOnTouchListener(NoScrollTouchListener(ctx) { mediaProvider.skipToNext() })
        fakePrev.setOnTouchListener(NoScrollTouchListener(ctx) { mediaProvider.skipToPrevious() })
        scrollView.setOnTouchListener(NoScrollTouchListener(ctx) { mediaProvider.playPause() })
        seekBar.setOnSeekBarChangeListener(seekBarListener)

        sync.setOnClickListener { _ ->
            OfflineLyricsSyncAdjustementDialog.show(ctx, presenter.getSyncAdjustement()) {
                presenter.updateSyncAdjustement(it)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        edit.setOnClickListener(null)
        back.setOnClickListener(null)
        search.setOnClickListener(null)
        act.window.setLightStatusBar()

        fakeNext.setOnTouchListener(null)
        fakePrev.setOnTouchListener(null)
        scrollView.setOnTouchListener(null)
        seekBar.setOnSeekBarChangeListener(null)
        sync.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        tutorialDisposable.unsubscribe()
        updateDisposable.unsubscribe()
        blurLayout.pauseBlur()
    }

    private fun searchLyrics(){
        val toolbarColor = R.color.toolbar
        val customTabIntent = CustomTabsIntent.Builder()
                .enableUrlBarHiding()
                .setToolbarColor(ContextCompat.getColor(ctx, toolbarColor))
                .build()
        CustomTabsHelper.addKeepAliveExtra(ctx, customTabIntent.intent)

        val escapedQuery = URLEncoder.encode(presenter.getInfoMetadata(), "UTF-8")
        val uri = Uri.parse("http://www.google.com/#q=$escapedQuery")
        CustomTabsHelper.openCustomTab(ctx, customTabIntent, uri) { _, _ ->
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (act.packageManager.isIntentSafe(intent)) {
                startActivity(intent)
            } else {
                act.toast(R.string.common_browser_not_found)
            }
        }
    }


    private fun handleSeekBarState(isPlaying: Boolean, speed: Float){
        updateDisposable.unsubscribe()
        if (isPlaying) {
            resumeSeekBar(speed)
        }
    }

    private fun resumeSeekBar(speed: Float){
        updateDisposable = Observable.interval(AppConstants.PROGRESS_BAR_INTERVAL.toLong(), TimeUnit.MILLISECONDS)
                .subscribe({ seekBar.incrementProgressBy((AppConstants.PROGRESS_BAR_INTERVAL * speed).toInt()) }, Throwable::printStackTrace)
    }

    private val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            mediaProvider.seekTo(seekBar.progress.toLong())
        }
    }


    override fun provideLayoutId(): Int = R.layout.fragment_offline_lyrics
}