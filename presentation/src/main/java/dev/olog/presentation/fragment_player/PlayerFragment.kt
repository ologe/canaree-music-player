package dev.olog.presentation.fragment_player

import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.presentation.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation.SeekBarObservable
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.model.CoverModel
import dev.olog.presentation.model.PlayerFragmentMetadata
import dev.olog.presentation.service_music.MusicController
import dev.olog.presentation.utils.TextUtils
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.widgets.SwipeableImageView
import dev.olog.shared.unsubscribe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.ofType
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player.view.*
import kotlinx.android.synthetic.main.layout_player_toolbar.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlayerFragment : BaseFragment() {

    @Inject lateinit var viewModel: PlayerFragmentViewModel
    @Inject lateinit var musicController: MusicController

    private var updateDisposable : Disposable? = null

    lateinit var title: TextView
    lateinit var artist: TextView
    lateinit var isExplicit: View
    lateinit var isRemix: View

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val container = activity!!.findViewById<ViewGroup>(R.id.playingQueueLayout)
        title = container.findViewById(R.id.title)
        artist = container.findViewById(R.id.artist)
        isExplicit = container.findViewById(R.id.explicit)
        isRemix = container.findViewById(R.id.remix)

        viewModel.onMetadataChangedLiveData
                .subscribe(this, this::setMetadata)

        viewModel.onCoverChangedLiveData
                .subscribe(this, this::setCover)

        viewModel.onPlaybackStateChangedLiveData
                .subscribe(this, {
                    handleSeekbarState(it)
                    nowPlaying.isActivated = it
                    cover.isActivated = it
                    coverLayout.isActivated = it
                })

        viewModel.onRepeatModeChangedLiveData
                .subscribe(this, {
                    repeat.setImageResource(if (it == PlaybackStateCompat.REPEAT_MODE_ONE)
                        R.drawable.vd_repeat_one else R.drawable.vd_repeat)
                    repeat.setColorFilter(ContextCompat.getColor(context!!,
                            if (it == PlaybackStateCompat.REPEAT_MODE_NONE) R.color.button_primary_tint
                            else R.color.item_selected))
                })

        viewModel.onShuffleModeChangedLiveData
                .subscribe(this , {
                    shuffle.setColorFilter(ContextCompat.getColor(context!!,
                            if (it == PlaybackStateCompat.SHUFFLE_MODE_NONE) R.color.button_primary_tint
                            else R.color.item_selected))
                })

        viewModel.onMaxChangedObservable
                .subscribe(this, {
                    duration.text = it.asString
                    seekBar.max = it.asInt
                })

//        bookmark textView will automatically updated
        viewModel.onBookmarkChangedObservable
                .subscribe(this, { seekBar.progress = it })

        viewModel.onFavoriteStateChangedObservable
                .subscribe(this, { favorite.toggleFavorite(it) })

        viewModel.onFavoriteAnimateRequestObservable
                .subscribe(this, { favorite.animateFavorite(it) })


        val seekBarObservable = SeekBarObservable(seekBar).share()

        seekBarObservable
                .ofType<Int>()
                .map { it.toLong() }
                .map { TextUtils.getReadableSongLength(it) }
                .asLiveData()
                .subscribe(this, {
                    bookmark.text = it
                })

        seekBarObservable.ofType<Pair<SeekBarObservable.Notification, Int>>()
                .filter { (notification, _) -> notification == SeekBarObservable.Notification.STOP }
                .map { (_, progress) -> progress.toLong() }
                .asLiveData()
                .subscribe(this, musicController::seekTo)

        RxView.clicks(repeat)
                .asLiveData()
                .subscribe(this, { musicController.toggleRepeatMode() })

        RxView.clicks(shuffle)
                .asLiveData()
                .subscribe(this, { musicController.toggleShuffleMode() })

        RxView.clicks(fakeNext)
                .asLiveData()
                .subscribe(this, { musicController.skipToNext() })

        RxView.clicks(fakePrevious)
                .asLiveData()
                .subscribe(this, { musicController.skipToPrevious() })

        RxView.clicks(favorite)
                .asLiveData()
                .subscribe(this, { musicController.togglePlayerFavorite() })
    }

    override fun onResume() {
        super.onResume()
        cover.setOnSwipeListener(object : SwipeableImageView.SwipeListener {

            override fun onSwipedLeft() {
                musicController.skipToNext()
            }

            override fun onSwipedRight() {
                musicController.skipToPrevious()
            }

            override fun onClick() {
                musicController.playPause()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        cover.setOnSwipeListener(null)
    }

    private fun handleSeekbarState(isPlaying: Boolean){
        updateDisposable.unsubscribe()
        if (isPlaying) {
            resumeSeekBar()
        }
    }

    private fun resumeSeekBar(){
        updateDisposable = Observable.interval(250, TimeUnit.MILLISECONDS)
                .subscribe({ view!!.seekBar.incrementProgressBy(250) }, Throwable::printStackTrace)
    }

    private fun setMetadata(metadata: PlayerFragmentMetadata){
        title.text = metadata.title
        artist.text = metadata.artist
        isExplicit.visibility = if (metadata.isExplicit) View.VISIBLE else View.GONE
        isRemix.visibility = if (metadata.isRemix) View.VISIBLE else View.GONE
    }

    private fun setCover(coverModel: CoverModel){
        val (img, placeholder) = coverModel
        GlideApp.with(context).clear(cover)

        GlideApp.with(context)
                .load(img)
                .centerCrop()
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .priority(Priority.IMMEDIATE)
                .override(800)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        if (placeholder is TransitionDrawable){
                            placeholder.startTransition(300)
                        }
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        return false
                    }
                }).into(cover)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_player
}