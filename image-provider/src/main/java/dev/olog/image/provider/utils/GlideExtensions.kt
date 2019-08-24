package dev.olog.image.provider.utils

import dev.olog.core.MediaId
import dev.olog.core.gateway.CachedImageVersion
import dev.olog.image.provider.GlideRequest
import dev.olog.image.provider.loader.MediaIdKey

fun <T> GlideRequest<T>.tryAddSignature(mediaId: MediaId): GlideRequest<T> {
    val version = CachedImageVersion.map[mediaId]
    if (version != null){
//         changed image at runtime
        return this.signature(MediaIdKey(mediaId, version))
    }
//     first load,
    return this.signature(MediaIdKey(mediaId, 0))
}

fun <T> GlideRequest<T>.firstVersionSignature(mediaId: MediaId): GlideRequest<T> {
    return this.signature(MediaIdKey(mediaId, 0))
}