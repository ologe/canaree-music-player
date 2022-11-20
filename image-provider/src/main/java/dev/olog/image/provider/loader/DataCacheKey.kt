package dev.olog.image.provider.loader

import com.bumptech.glide.load.Key
import com.bumptech.glide.signature.EmptySignature

@Suppress("FunctionName")
internal fun DataCacheKey(sourceKey: Key, signature: Key = EmptySignature.obtain()): Key {
    val cls = Class.forName("com.bumptech.glide.load.engine.DataCacheKey")
    val constructor = cls.getDeclaredConstructor(Key::class.java, Key::class.java)
    constructor.isAccessible = true
    val instance = constructor.newInstance(sourceKey, signature) as Key
    constructor.isAccessible = false
    return instance
}