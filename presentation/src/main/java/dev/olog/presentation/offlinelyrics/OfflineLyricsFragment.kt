package dev.olog.presentation.offlinelyrics

import android.content.Intent
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import dev.olog.media.*
import dev.olog.offlinelyrics.NoScrollTouchListener
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.interfaces.DrawsOnTop
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.presentation.utils.removeLightStatusBar
import dev.olog.presentation.utils.setLightStatusBar
import dev.olog.shared.extensions.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_offline_lyrics.*
import kotlinx.android.synthetic.main.fragment_offline_lyrics.view.*
import saschpe.android.customtabs.CustomTabsHelper
import java.net.URLEncoder
import javax.inject.Inject

class OfflineLyricsFragment : BaseFragment(), DrawsOnTop {

    companion object {
        const val TAG = "OfflineLyricsFragment"

        @JvmStatic
        fun newInstance(): OfflineLyricsFragment {
            return OfflineLyricsFragment()
        }
    }

    @Inject
    lateinit var presenter: OfflineLyricsFragmentPresenter
    private var tutorialDisposable: Disposable? = null

    private val mediaProvider by lazy { activity as MediaProvider }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tutorialDisposable = presenter.showAddLyricsIfNeverShown()
            .subscribe({ TutorialTapTarget.addLyrics(view.search, view.edit, view.sync) }, {})

        seekBar.setListener(onStopTouch = {
            mediaProvider.seekTo(seekBar.progress.toLong())
        }, onStartTouch = {
        }, onProgressChanged = {
        })

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
            .subscribe(viewLifecycleOwner) { seekBar.onStateChanged(it) }

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
            dev.olog.offlinelyrics.EditLyricsDialog.show(
                act,
                presenter.getOriginalLyrics()
            ) { newLyrics ->
                presenter.updateLyrics(newLyrics)
            }
        }
        back.setOnClickListener { act.onBackPressed() }
        search.setOnClickListener { searchLyrics() }
        act.window.removeLightStatusBar()

        fakeNext.setOnTouchListener(NoScrollTouchListener(ctx) { mediaProvider.skipToNext() })
        fakePrev.setOnTouchListener(NoScrollTouchListener(ctx) { mediaProvider.skipToPrevious() })
        scrollView.setOnTouchListener(NoScrollTouchListener(ctx) { mediaProvider.playPause() })

        sync.setOnClickListener { _ ->
            dev.olog.offlinelyrics.OfflineLyricsSyncAdjustementDialog.show(
                ctx,
                presenter.getSyncAdjustement()
            ) {
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
        blurLayout.pauseBlur()
    }

    private fun searchLyrics() {
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


    override fun provideLayoutId(): Int = R.layout.fragment_offline_lyrics
}