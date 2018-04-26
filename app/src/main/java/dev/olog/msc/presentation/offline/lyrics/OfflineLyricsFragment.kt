package dev.olog.msc.presentation.offline.lyrics

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.player.EditLyricsDialog
import dev.olog.msc.presentation.tutorial.TutorialTapTarget
import dev.olog.msc.presentation.utils.animation.CircularReveal
import dev.olog.msc.presentation.utils.animation.HasSafeTransition
import dev.olog.msc.presentation.utils.animation.SafeTransition
import dev.olog.msc.utils.k.extension.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_offline_lyrics.*
import kotlinx.android.synthetic.main.fragment_offline_lyrics.view.*
import java.net.URLEncoder
import javax.inject.Inject

class OfflineLyricsFragment : BaseFragment(), HasSafeTransition {

    companion object {
        const val TAG = "OfflineLyricsFragment"
        private const val ARGUMENT_ICON_POS_X = "$TAG.argument.pos.x"
        private const val ARGUMENT_ICON_POS_Y = "$TAG.argument.pos.y"

        @JvmStatic
        fun newInstance(icon: View): OfflineLyricsFragment {
            val x = (icon.x + icon.width / 2).toInt()
            val y = (icon.y + icon.height / 2).toInt()
            return OfflineLyricsFragment().withArguments(
                    ARGUMENT_ICON_POS_X to x,
                    ARGUMENT_ICON_POS_Y to y
            )
        }
    }

    @Inject lateinit var presenter: OfflineLyricsFragmentPresenter
    @Inject lateinit var safeTransition: SafeTransition
    private var tutorialDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null){
            val x = arguments!!.getInt(ARGUMENT_ICON_POS_X)
            val y = arguments!!.getInt(ARGUMENT_ICON_POS_Y)
            safeTransition.execute(this, CircularReveal(ctx, x, y, toColor = Color.BLACK))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mediaProvider = activity as MediaProvider

        mediaProvider.onMetadataChanged()
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .asLiveData()
                .subscribe(this, {
                    presenter.updateCurrentTrackId(it.getId())
                    presenter.updateCurrentMetadata(it.getTitle().toString(), it.getArtist().toString())
                    image.loadImage(it)
                    header.text = it.getTitle()
                    subHeader.text = it.getArtist()
                })

        presenter.observeLyrics {
            if (it.isBlank()) getString(R.string.offline_lyrics_empty) else it
        }.observeOn(AndroidSchedulers.mainThread())
                .asLiveData()
                .subscribe(this, text::setText)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        super.onViewBound(view, savedInstanceState)
        postponeEnterTransition()
        view.image.post { startPostponedEnterTransition() }

        tutorialDisposable = presenter.showAddLyricsIfNeverShown()
                .subscribe({ TutorialTapTarget.addLyrics(view.search, view.edit) }, {})
    }

    override fun onResume() {
        super.onResume()
        edit.setOnClickListener {
            EditLyricsDialog.show(act, text.text.toString(), { newLyrics ->
                presenter.updateLyrics(newLyrics)
            })
        }
        back.setOnClickListener { act.onBackPressed() }
        search.setOnClickListener {
            val escapedQuery = URLEncoder.encode(presenter.getInfoMetadata(), "UTF-8")
            val uri = Uri.parse("http://www.google.com/#q=$escapedQuery")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (act.packageManager.isIntentSafe(intent)) {
                startActivity(intent)
            } else {
                act.toast(R.string.common_browser_not_found)
            }
        }
        act.window.removeLightStatusBar()
    }

    override fun onPause() {
        super.onPause()
        edit.setOnClickListener(null)
        back.setOnClickListener(null)
        search.setOnClickListener(null)
        act.window.setLightStatusBar()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
        tutorialDisposable.unsubscribe()
    }

    override fun isAnimating(): Boolean = safeTransition.isAnimating

    override fun provideLayoutId(): Int = R.layout.fragment_offline_lyrics
}