package dev.olog.presentation.offlinelyrics

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import dev.olog.offlinelyrics.NoScrollTouchListener
import dev.olog.media.MediaProvider
import dev.olog.offlinelyrics.EditLyricsDialog
import dev.olog.offlinelyrics.OfflineLyricsSyncAdjustementDialog
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.interfaces.DrawsOnTop
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.presentation.utils.removeLightStatusBar
import dev.olog.presentation.utils.setLightStatusBar
import dev.olog.shared.android.extensions.*
import kotlinx.android.synthetic.main.fragment_offline_lyrics.*
import kotlinx.android.synthetic.main.fragment_offline_lyrics.view.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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

    private val mediaProvider by lazy { activity as MediaProvider }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (presenter.showAddLyricsIfNeverShown()) {
            TutorialTapTarget.addLyrics(view.search, view.edit, view.sync)
        }

        seekBar.setListener(onStopTouch = {
            mediaProvider.seekTo(seekBar.progress.toLong())
        }, onStartTouch = {
        }, onProgressChanged = {
        })

        mediaProvider.observeMetadata()
            .subscribe(viewLifecycleOwner) {
                presenter.updateCurrentTrackId(it.id)
                presenter.updateCurrentMetadata(it.title, it.artist)
                image.loadImage(it.mediaId)
                header.text = it.title
                subHeader.text = it.artist
                seekBar.max = it.duration.toInt()
            }

        launch {
            presenter.observeLyrics()
                .map { presenter.transformLyrics(ctx, seekBar.progress, it) }
                .map { text.precomputeText(it) }
                .asLiveData()
                .subscribe(viewLifecycleOwner) {
                    emptyState.toggleVisibility(it.isEmpty(), true)
                    text.text = it
                }
        }

        mediaProvider.observePlaybackState()
            .filter { it.isPlayOrPause }
            .subscribe(viewLifecycleOwner) { seekBar.onStateChanged(it) }

        view.image.observePaletteColors()
            .map { it.accent }
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
            launch {
                EditLyricsDialog.show(act, presenter.getLyrics()) { newLyrics ->
                    presenter.updateLyrics(newLyrics)
                }
            }
        }
        back.setOnClickListener { act.onBackPressed() }
        search.setOnClickListener { searchLyrics() }
        act.window.removeLightStatusBar()

        fakeNext.setOnTouchListener(NoScrollTouchListener(ctx) { mediaProvider.skipToNext() })
        fakePrev.setOnTouchListener(NoScrollTouchListener(ctx) { mediaProvider.skipToPrevious() })
        scrollView.setOnTouchListener(NoScrollTouchListener(ctx) { mediaProvider.playPause() })

        sync.setOnClickListener { _ ->
            launch {
                OfflineLyricsSyncAdjustementDialog.show(
                    ctx,
                    presenter.getSyncAdjustment()
                ) {
                    presenter.updateSyncAdjustment(it)
                }
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
        blurLayout.pauseBlur()
    }

    private fun searchLyrics() {
        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(ctx.colorSurface())
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