package dev.olog.presentation.offlinelyrics

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.MediaId
import dev.olog.lib.image.loader.OnImageLoadingError
import dev.olog.lib.image.loader.getCachedBitmap
import dev.olog.lib.media.MediaProvider
import dev.olog.lib.offline.lyrics.EditLyricsDialog
import dev.olog.lib.offline.lyrics.OfflineLyricsSyncAdjustementDialog
import dev.olog.presentation.R
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.CanChangeStatusBarColor
import dev.olog.feature.presentation.base.FloatingWindow
import dev.olog.feature.presentation.base.extensions.*
import dev.olog.feature.presentation.base.extensions.removeLightStatusBar
import dev.olog.shared.android.extensions.*
import dev.olog.shared.throwNotHandled
import io.alterac.blurkit.BlurKit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import saschpe.android.customtabs.CustomTabsHelper
import timber.log.Timber
import java.net.URLEncoder
import javax.inject.Inject

@AndroidEntryPoint
class OfflineLyricsFragment : BaseFragment(),
    FloatingWindow,
    CanChangeStatusBarColor {

    companion object {
        @JvmStatic
        val TAG = OfflineLyricsFragment::class.java.name

        @JvmStatic
        fun newInstance(): OfflineLyricsFragment {
            return OfflineLyricsFragment()
        }
    }

    @Inject
    lateinit var presenter: OfflineLyricsFragmentPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (presenter.showAddLyricsIfNeverShown()) {
//            TutorialTapTarget.addLyrics(view.search, view.edit, view.sync) TODO tutorial
        }

//        list.onTap = {
//            mediaProvider.playPause()
//        }

        mediaProvider.observeMetadata()
            .onEach {
                presenter.updateCurrentTrackId(it.id.toLong())
                presenter.updateCurrentMetadata(it.title, it.artist)
//                textWrapper.update(it.title, it.artist)
//                seekBar.max = it.duration.toInt()
                loadImage(it.mediaId)

                if (presenter.firstEnter) {
                    presenter.firstEnter = false
//                    list.scrollToCurrent()
                } else {
//                    list.smoothScrollToPosition(0)
                }
            }.launchIn(lifecycleScope)

        presenter.observeLyrics()
            .onEach {
//                list.adapter.suspendSubmitList(it.lines)
//                list.awaitAnimationEnd()
//                emptyState.isVisible = it.lines.isEmpty()
            }.launchIn(viewLifecycleOwner.lifecycleScope)

//        seekBar.observeProgress()
//            .onEach { list.adapter.updateTime(it) }
//            .launchIn(viewLifecycleOwner.lifecycleScope)

//        mediaProvider.observePlaybackState()
//            .filter { it.isPlayOrPause }
//            .onEach { seekBar.onStateChanged(it) }
//            .launchIn(viewLifecycleOwner.lifecycleScope)

//        view.image.observePaletteColors()
//            .map { it.accent }
//            .onEach { accent ->
//                artist.animateTextColor(accent)
//                edit.animateBackgroundColor(accent)
//            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onResume() {
        super.onResume()
//        edit.onClick {
//            EditLyricsDialog.show(requireActivity(), presenter.getLyrics()) { newLyrics ->
//                presenter.updateLyrics(newLyrics)
//            }
//        }
//        back.setOnClickListener { requireActivity().onBackPressed() }
//        search.setOnClickListener { searchLyrics() }

//        fakeNext.setOnClickListener {
//            list.adapter.debounceUpdate()
//            mediaProvider.skipToNext()
//        }
//        fakePrev.setOnClickListener {
//            list.adapter.debounceUpdate()
//            mediaProvider.skipToPrevious()
//        }

//        sync.onClick { _ ->
//            try {
//                OfflineLyricsSyncAdjustementDialog.show(
//                    requireContext(),
//                    presenter.getSyncAdjustment()
//                ) {
//                    presenter.updateSyncAdjustment(it)
//                }
//            } catch (ex: Exception){
//                Timber.e(ex)
//            }
//        }

//        seekBar.setListener(onStopTouch = {
//            mediaProvider.seekTo(seekBar.progress.toLong())
//        })
    }

    override fun onPause() {
        super.onPause()
//        edit.setOnClickListener(null)
//        back.setOnClickListener(null)
//        search.setOnClickListener(null)
//
//        fakeNext.setOnTouchListener(null)
//        fakePrev.setOnTouchListener(null)
//        seekBar.setOnSeekBarChangeListener(null)
//        sync.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.dispose()
    }

    override fun adjustStatusBarColor() {
        requireActivity().window.removeLightStatusBar()
    }

    override fun adjustStatusBarColor(lightStatusBar: Boolean) {
        throwNotHandled("don't call me")
    }

    @SuppressLint("ConcreteDispatcherIssue")
    private suspend fun loadImage(mediaId: MediaId) = withContext(Dispatchers.IO){
        try {
            val original = requireContext().getCachedBitmap(mediaId, 300, onError = OnImageLoadingError.Placeholder(true))
            val blurred = BlurKit.getInstance().blur(original, 20)
            withContext(Dispatchers.Main){
//                image.setImageBitmap(blurred)
            }
        } catch (ex: Exception){
            Timber.e(ex)
        }
    }

    private fun searchLyrics() {
        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(requireContext().colorSurface())
            .build()
        CustomTabsHelper.addKeepAliveExtra(requireContext(), customTabIntent.intent)

        val escapedQuery = URLEncoder.encode(presenter.getInfoMetadata(), "UTF-8")
        val uri = Uri.parse("http://www.google.com/#q=$escapedQuery")
        CustomTabsHelper.openCustomTab(requireContext(), customTabIntent, uri, object : CustomTabsHelper.CustomTabFallback {
            override fun openUri(context: Context?, uri: Uri?) {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                if (requireActivity().packageManager.isIntentSafe(intent)) {
                    startActivity(intent)
                } else {
                    requireActivity().toast(R.string.common_browser_not_found)
                }
            }
        })
    }


    override fun provideLayoutId(): Int = R.layout.fragment_offline_lyrics
}