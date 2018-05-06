package dev.olog.msc.presentation.offline.lyrics

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.media.MediaMetadataCompat
import android.view.View
import android.widget.SeekBar
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.text.isDigitsOnly
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
import dev.olog.msc.presentation.utils.blur.FastBlur
import dev.olog.msc.presentation.widget.image.view.toPlayerImage
import dev.olog.msc.theme.ThemedDialog
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_offline_lyrics_2.*
import kotlinx.android.synthetic.main.fragment_offline_lyrics_2.view.*
import kotlinx.android.synthetic.main.fragment_player_controls.view.*
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
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
    private var updateDisposable : Disposable? = null

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
                    seekBar.max = it.getDuration().toInt()
                })

        presenter.observeLyrics()
                .map { presenter.transformLyrics(ctx, seekBar.progress, it) }
                .observeOn(AndroidSchedulers.mainThread())
                .asLiveData()
                .subscribe(this, {
                    emptyState.toggleVisibility(it.isEmpty(), true)
                    text.setText(it)
                })

        mediaProvider.onStateChanged()
                .filter { it.state == PlaybackState.STATE_PLAYING || it.state == PlaybackState.STATE_PAUSED }
                .asLiveData()
                .subscribe(this, {
                    val isPlaying = it.state == PlaybackState.STATE_PLAYING
                    seekBar.progress = it.position.toInt()
                    handleSeekBarState(isPlaying)
                })

    }

    private fun loadBackgroundImage(metadata: MediaMetadataCompat){
        val model = metadata.toPlayerImage()
        val mediaId = metadata.getMediaId()
        GlideApp.with(ctx).clear(image)

        val radius = 8
        val sampling = 6

        val drawable = CoverUtils.getGradient(ctx, mediaId)
        val bitmap = drawable.toBitmap(100, 100, Bitmap.Config.RGB_565)
        val placeholder = FastBlur.blur(bitmap, radius, false)
                .toDrawable(resources)

        GlideApp.with(ctx)
                .load(model)
                .placeholder(placeholder)
                .priority(Priority.IMMEDIATE)
                .transform(BlurTransformation(radius, sampling))
                .override(500)
                .into(image)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        super.onViewBound(view, savedInstanceState)
        postponeEnterTransition()
        view.image.post { startPostponedEnterTransition() }

        tutorialDisposable = presenter.showAddLyricsIfNeverShown()
                .subscribe({ TutorialTapTarget.addLyrics(view.search, view.edit, view.sync) }, {})

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

        fakeNext.setOnTouchListener(CustomTouchListener(ctx, { mediaProvider.skipToNext() }))
        fakePrev.setOnTouchListener(CustomTouchListener(ctx, { mediaProvider.skipToPrevious() }))
        seekBar.setOnSeekBarChangeListener(seekBarListener)

        sync.setOnClickListener {
            val builder = ThemedDialog.builder(ctx)
                    .setTitle(R.string.offline_lyrics_adjust_sync)
                    .setView(R.layout.layout_edit_text_simple)
                    .setPositiveButton(R.string.popup_positive_ok, null)
                    .setNegativeButton(R.string.popup_negative_cancel, null)

            val dialog = builder.makeDialog()

            val editText = dialog.findViewById<TextInputEditText>(R.id.editText)
            val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)
            editTextLayout.hint = getString(R.string.offline_lyrics_adjust_sync_hint)
            editText.setText(presenter.getSyncAdjustement())

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val text = editText.text.toString()
                if (text.isDigitsOnly() || (text.isNotBlank() && text[0] == '-' && text.substring(1).isDigitsOnly())){
                    presenter.updateSyncAdjustement(text.toLong())
                    dialog.dismiss()
                } else {
                    toast(getString(R.string.offline_lyrics_adjust_sync_error))
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
        seekBar.setOnSeekBarChangeListener(null)
        sync.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
        tutorialDisposable.unsubscribe()
        updateDisposable.unsubscribe()
    }

    private fun handleSeekBarState(isPlaying: Boolean){
        updateDisposable.unsubscribe()
        if (isPlaying) {
            resumeSeekBar()
        }
    }

    private fun resumeSeekBar(){
        updateDisposable = Observable.interval(250L, TimeUnit.MILLISECONDS)
                .subscribe({ seekBar.incrementProgressBy(250) }, Throwable::printStackTrace)
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

    override fun isAnimating(): Boolean = safeTransition.isAnimating

    override fun provideLayoutId(): Int = R.layout.fragment_offline_lyrics_2
}