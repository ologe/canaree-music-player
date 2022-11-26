package dev.olog.image.provider.decoder

import android.graphics.Bitmap
import android.graphics.drawable.LayerDrawable
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource

class LayerDrawableBitmapDecoder(
    private val bitmapPool: BitmapPool,
) : ResourceDecoder<LayerDrawable, Bitmap> {

    override fun handles(source: LayerDrawable, options: Options): Boolean = true

    override fun decode(
        source: LayerDrawable,
        width: Int,
        height: Int,
        options: Options
    ): Resource<Bitmap>? {
        return BitmapResource.obtain(source.toBitmap(), bitmapPool)
    }
}