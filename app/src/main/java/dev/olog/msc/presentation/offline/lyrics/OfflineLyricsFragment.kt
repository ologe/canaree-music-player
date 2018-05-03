package dev.olog.msc.presentation.offline.lyrics

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.view.View
import androidx.core.view.doOnPreDraw
import com.bumptech.glide.Priority
import dev.olog.msc.R
import dev.olog.msc.app.GlideApp
import dev.olog.msc.glide.transformation.BlurTransformation
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.player.EditLyricsDialog
import dev.olog.msc.presentation.tutorial.TutorialTapTarget
import dev.olog.msc.presentation.utils.animation.CircularReveal
import dev.olog.msc.presentation.utils.animation.HasSafeTransition
import dev.olog.msc.presentation.utils.animation.SafeTransition
import dev.olog.msc.presentation.widget.image.view.toPlayerImage
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_offline_lyrics_2.*
import kotlinx.android.synthetic.main.fragment_offline_lyrics_2.view.*
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

    private lateinit var mediaProvider: MediaProvider

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
        mediaProvider = activity as MediaProvider

        mediaProvider.onMetadataChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .asLiveData()
                .subscribe(this, {
                    presenter.updateCurrentTrackId(it.getId())
                    presenter.updateCurrentMetadata(it.getTitle().toString(), it.getArtist().toString())
                    loadBackgroundImage(it)
                    header.text = it.getTitle()
                    subHeader.text = it.getArtist()
                })

        presenter.observeLyrics()
                .observeOn(AndroidSchedulers.mainThread())
                .asLiveData()
                .subscribe(this, {
                    emptyState.toggleVisibility(it.isEmpty(), true)
                    text.text = it
                })

    }

    private fun loadBackgroundImage(metadata: MediaMetadataCompat){
        val model = metadata.toPlayerImage()
        val mediaId = metadata.getMediaId()
        GlideApp.with(ctx).clear(image)

        GlideApp.with(ctx)
                .load(model)
                .placeholder(CoverUtils.getGradient(ctx, mediaId))
                .priority(Priority.IMMEDIATE)
                .transform(BlurTransformation(10, 6))
                .override(800)
                .into(image)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        super.onViewBound(view, savedInstanceState)
        postponeEnterTransition()
        view.image.post { startPostponedEnterTransition() }

        tutorialDisposable = presenter.showAddLyricsIfNeverShown()
                .subscribe({ TutorialTapTarget.addLyrics(view.search, view.edit) }, {})

        view.scrollView.doOnPreDraw {
            it.setPaddingTop(view.statusBar.height * 3)
        }

        view.scrollView.setClickBehavior { mediaProvider.playPause() }
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

        fakeNext.setOnClickListener { mediaProvider.skipToNext() }
        fakePrev.setOnClickListener { mediaProvider.skipToPrevious() }
    }

    override fun onPause() {
        super.onPause()
        edit.setOnClickListener(null)
        back.setOnClickListener(null)
        search.setOnClickListener(null)
        act.window.setLightStatusBar()

        fakeNext.setOnClickListener(null)
        fakePrev.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
        tutorialDisposable.unsubscribe()
    }

    override fun isAnimating(): Boolean = safeTransition.isAnimating

    override fun provideLayoutId(): Int = R.layout.fragment_offline_lyrics_2
}