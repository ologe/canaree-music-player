package dev.olog.core.comparator

import android.provider.MediaStore
import dev.olog.shared.android.utils.isNougat

/**
 * strength:
 *  - PRIMARY ~> character difference: "a" < "b"
 *  - SECONDARY ~> accent difference: "as" < "às" < "at"
 *  - TERTIARY ~> case difference: "ao" < "Ao" < "aò"
 *  - QUATERNARY ~> punctuation difference: "ab" < "a-b" < "aB"
 *
 *  decomposition
 *  - NO_DECOMPOSITION ~> no decomposition for collation, should be done by client
 *  - CANONICAL_DECOMPOSITION ~> decompose character according to Unicode standards
 */
// TODO test performance
internal object CanareeCollator {

    private val delegate: Comparator<Any> = when {
        isNougat() -> android.icu.text.Collator.getInstance().apply {
            strength = android.icu.text.Collator.QUATERNARY
            decomposition = android.icu.text.Collator.CANONICAL_DECOMPOSITION
        }

        else -> java.text.Collator.getInstance().apply {
            strength = java.text.Collator.TERTIARY
            decomposition = java.text.Collator.CANONICAL_DECOMPOSITION
        }
    }

    private val comparator = Comparator<Any> { o1, o2 ->
        if (o1 == MediaStore.UNKNOWN_STRING && o1 == o2) {
            // both are <unknown>
            return@Comparator 0
        }
        if (o1 == MediaStore.UNKNOWN_STRING) {
            // o1 <unknown>, move to last
            return@Comparator 1
        }
        if (o2 == MediaStore.UNKNOWN_STRING) {
            // o2 <unknown>, move to first
            return@Comparator -1
        }
        return@Comparator delegate.compare(o1, o2)
    }

    fun <T> compareBy(
        vararg selectors: (T) -> Comparable<*>
    ): Comparator<T> {
        return Comparator { o1, o2 ->
            compareValuesBy(o1, o2, *selectors)
        }
    }

    fun <T> compareByDescending(
        vararg selectors: (T) -> Comparable<*>
    ): Comparator<T> {
        return Comparator { o1, o2 ->
            compareValuesBy(o2, o1, *selectors)
        }
    }

    private fun <T> compareValuesBy(a: T, b: T, vararg selectors: (T) -> Comparable<*>): Int {
        for (fn in selectors) {
            val v1 = fn(a)
            val v2 = fn(b)
            val diff = comparator.compare(v1, v2)
            if (diff != 0) {
                return diff
            }
        }
        return 0
    }

}