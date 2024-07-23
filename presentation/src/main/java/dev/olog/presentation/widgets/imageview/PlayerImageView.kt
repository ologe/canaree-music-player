package dev.olog.presentation.widgets.imageview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.bumptech.glide.Priority
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.shared.widgets.image.ShapeImageView
import javax.inject.Inject

@AndroidEntryPoint
class PlayerImageView : ShapeImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    @Inject
    lateinit var adaptiveImageHelper: AdaptiveImageHelper

    private var lastMediaId: MediaId? = null

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (!isInEditMode) {
            adaptiveImageHelper.setImageBitmap(bm)
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (!isInEditMode) {
            adaptiveImageHelper.setImageDrawable(drawable)
        }
    }

    fun loadImage(mediaId: MediaId) {
        val previousMediaId = lastMediaId
        lastMediaId = mediaId
        if (mediaId == previousMediaId) return
        GlideApp.with(context).clear(this)

        GlideApp.with(context)
            .load(mediaId)
            .error(CoverUtils.getGradient(context, mediaId))
            .priority(Priority.IMMEDIATE)
            .override(500)
            .into(this)
    }

}

