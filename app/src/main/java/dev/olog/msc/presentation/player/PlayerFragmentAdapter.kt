package dev.olog.msc.presentation.player

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.dagger.FragmentLifecycle
import dev.olog.msc.presentation.GlideApp
import dev.olog.msc.presentation.SeekBarObservable
import dev.olog.msc.presentation.base.adapter.BaseListAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.widget.SwipeableView
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.ofType
import kotlinx.android.synthetic.main.fragment_player_controls.view.*
import kotlinx.android.synthetic.main.fragment_player_toolbar.view.*
import kotlinx.android.synthetic.main.layout_swipeable_view.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlayerFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val mediaProvider: MediaProvider,
        private val navigator: Navigator

): BaseListAdapter<DisplayableItem>(lifecycle) {

    private var seekBarDisposable : Disposable? = null

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        when (viewType){
            R.layout.item_mini_queue -> {
                viewHolder.setOnClickListener(dataController) { item, _ ->
                    mediaProvider.skipToQueueItem(item.mediaId.leaf!!)
                }
                viewHolder.setOnLongClickListener(dataController) { item, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, dataController) { item, _, view ->
                    navigator.toDialog(item, view)
                }
                viewHolder.elevateSongOnTouch()
            }
        }
    }

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder<*>) {
        val viewType = holder.itemViewType
        when (viewType){
            R.layout.fragment_player_controls -> {
                bindPlayerControls(holder.itemView)
            }
        }
    }

    private fun bindPlayerControls(view: View){
        mediaProvider.onMetadataChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(RxView.detaches(view))
                .subscribe({
                    updateMetadata(view, it)
                    updateImage(view, it)
                }, Throwable::printStackTrace)

        mediaProvider.onStateChanged()
                .takeUntil(RxView.detaches(view))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onPlaybackStateChanged(view, it)
                }, Throwable::printStackTrace)

        if (view.repeat != null){
            mediaProvider.onRepeatModeChanged()
                    .takeUntil(RxView.detaches(view))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view.repeat::cycle, Throwable::printStackTrace)

            RxView.clicks(view.repeat)
                    .takeUntil(RxView.detaches(view))
                    .subscribe({ mediaProvider.toggleRepeatMode() }, Throwable::printStackTrace)
        }
        if (view.shuffle != null){
            mediaProvider.onShuffleModeChanged()
                    .takeUntil(RxView.detaches(view))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view.shuffle::cycle, Throwable::printStackTrace)


            RxView.clicks(view.shuffle)
                    .takeUntil(RxView.detaches(view))
                    .subscribe({ mediaProvider.toggleShuffleMode() }, Throwable::printStackTrace)
        }

        val seekBarObservable = SeekBarObservable(view.seekBar)
                .takeUntil(RxView.detaches(view))
                .share()

        seekBarObservable.ofType<Int>()
                .map { TextUtils.getReadableSongLength(it) }
                .subscribe(view.bookmark::setText, Throwable::printStackTrace)

        seekBarObservable.ofType<Pair<SeekBarObservable.Notification, Int>>()
                .filter { (notification, _) -> notification == SeekBarObservable.Notification.STOP }
                .map { (_, progress) -> progress.toLong() }
                .subscribe(mediaProvider::seekTo, Throwable::printStackTrace)

        view.swipeableView?.setOnSwipeListener(object : SwipeableView.SwipeListener{
            override fun onSwipedLeft() {
                mediaProvider.skipToNext()
            }

            override fun onSwipedRight() {
                mediaProvider.skipToPrevious()
            }

            override fun onClick() {
                mediaProvider.playPause()
            }

            override fun onLeftEdgeClick() {
                mediaProvider.skipToPrevious()
            }

            override fun onRightEdgeClick() {
                mediaProvider.skipToNext()
            }
        })
    }

    private fun updateMetadata(view: View, metadata: MediaMetadataCompat){
        view.title.text = metadata.getText(MediaMetadataCompat.METADATA_KEY_TITLE)
        view.artist.text = metadata.getText(MediaMetadataCompat.METADATA_KEY_ARTIST)

        val duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
        val durationAsString = TextUtils.MIDDLE_DOT_SPACED + TextUtils.getReadableSongLength(duration)
        view.duration.text = durationAsString
        view.seekBar.max = duration.toInt()
    }

    private fun updateImage(view: View, metadata: MediaMetadataCompat){
        view.cover ?: return

        val context = view.context
        val image = Uri.parse(metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI))
        val id = MediaId.fromString(
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
        ).leaf!!.toInt()
        val placeholder = CoverUtils.getGradient(context, id)
        GlideApp.with(context!!).clear(view.cover)

        GlideApp.with(context)
                .load(image)
                .centerCrop()
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .priority(Priority.IMMEDIATE)
                .override(800)
                .into(view.cover)
    }

    private fun onPlaybackStateChanged(view: View, playbackState: PlaybackStateCompat){
        val state = playbackState.state
        if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_PAUSED){
            val isPlaying = state == PlaybackStateCompat.STATE_PLAYING
            view.nowPlaying.isActivated = isPlaying
            view.coverLayout?.isActivated = isPlaying
            handleSeekBarState(view, isPlaying)
        }

        view.seekBar.progress = playbackState.position.toInt()
    }

    private fun handleSeekBarState(view: View, isPlaying: Boolean){
        seekBarDisposable.unsubscribe()

        if (isPlaying){
            seekBarDisposable = Observable.interval(250, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ view.seekBar.incrementProgressBy(250) }, Throwable::printStackTrace)
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }
}