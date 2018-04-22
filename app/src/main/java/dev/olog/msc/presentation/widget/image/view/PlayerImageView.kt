package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.msc.app.GlideApp
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.utils.images.ImageProcessor
import dev.olog.msc.presentation.utils.images.ImageProcessorResult
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.*
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers

class PlayerImageView @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null

) : RoundedCornersImageView(context, attr) {

    private var disposable: Disposable? = null
    private val colorsPublisher = BehaviorProcessor.create<ImageProcessorResult>()

    fun loadImage(metadata: MediaMetadataCompat){
        val mediaId = metadata.getMediaId()

        val model = metadata.toDisplayableItem()

        GlideApp.with(context).clear(this)

        GlideApp.with(context)
                .load(model)
                .placeholder(CoverUtils.getGradient(context, mediaId))
                .priority(Priority.IMMEDIATE)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(800)
                .into(this)

        if (!AppConstants.IS_ADAPTIVE_COLOR){
            // not need to waste resources
            return
        }

        disposable.unsubscribe()
        disposable = Single.fromCallable { true }
                .map { context.getBitmapAsync(model, 200) }
                .subscribeOn(Schedulers.io())
                .map { ImageProcessor(context).processImage(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(colorsPublisher::onNext, Throwable::printStackTrace)
    }

    fun toggleElevation(state: PlaybackStateCompat){
        if (state.isPlaying() || state.isPaused()){
            isActivated = state.isPlaying()
        }
    }

    fun observeImageColors(): Flowable<ImageProcessorResult> = colorsPublisher

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposable.unsubscribe()
    }

    private fun MediaMetadataCompat.toDisplayableItem(): DisplayableItem {
        // only mediaId and image is needed
        return DisplayableItem(0, this.getMediaId(), "", image = this.getImage())
    }

}