package dev.olog.shared.android

import android.os.Bundle

class BundleDictionary(
    private val map: Map<String, Any?>
) : Map<String, Any?> by map {

    inline fun <reified T> getTyped(key: String): T? {
        return get(key) as T?
    }

    override fun toString(): String = map.toString()

    override fun equals(other: Any?): Boolean = map == other

    override fun hashCode(): Int = map.hashCode()

}

// TODO rename toBundleDictionary
fun Bundle?.toMap(): BundleDictionary {
    this ?: return BundleDictionary(emptyMap())

    val map = this.keySet().map { it to get(it) }.toMap()
    return BundleDictionary(map)
}