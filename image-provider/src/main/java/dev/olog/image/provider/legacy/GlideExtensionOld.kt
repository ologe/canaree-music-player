package dev.olog.image.provider.legacy

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Priority
import dev.olog.core.MediaId
import dev.olog.core.gateway.getImageVersionGateway
import dev.olog.image.provider.*
import dev.olog.shared.android.utils.assertBackgroundThread

//TODO remove after migrating to coroutines
fun Context.getCachedBitmapOld(
    mediaId: MediaId,
    size: Int,
    extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)? = null,
    withError: Boolean = true
): Bitmap {

    assertBackgroundThread()

    val placeholder = CoverUtils.getGradient(this, mediaId)

    val error = GlideApp.with(this)
        .asBitmap()
        .load(placeholder.toBitmap())
        .override(size)
        .extend(extension)
        .signature(CustomMediaStoreSignature(mediaId, getImageVersionGateway()))

    val builder = GlideApp.with(this)
        .asBitmap()
        .load(mediaId)
        .override(size)
        .priority(Priority.IMMEDIATE)
        .onlyRetrieveFromCache(true)
        .extend(extension)
        .signature(CustomMediaStoreSignature(mediaId, getImageVersionGateway()))

    return try {
        builder.submit().get()
    } catch (ex: Throwable) {
        if (withError) {
            error.submit().get()
        } else {
            throw NullPointerException()
        }
    }

}