package dev.olog.feature.base.adapter

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter

@Suppress("nothing_to_inline")
inline fun CustomConcatAdapter.requireHeaderOf(adapter: Adapter<*>): OptionalAdapter<*> {
    return requireNotNull(headerOf(adapter)) { "header not found for $adapter" }
}

@Suppress("nothing_to_inline")
inline fun CustomConcatAdapter.headerOf(adapter: Adapter<*>): OptionalAdapter<*>? {
    return delegate.headerOf(adapter)
}

@Suppress("nothing_to_inline")
inline fun ConcatAdapter.requireHeaderOf(adapter: Adapter<*>): OptionalAdapter<*> {
    return requireNotNull(headerOf(adapter)) { "header not found for $adapter" }
}

/**
 * Header is the adapter before the requested [adapter]
 */
fun ConcatAdapter.headerOf(adapter: Adapter<*>): OptionalAdapter<*>? {
    val accumulator = mutableListOf<Adapter<*>>()
    flattenAdaptersRecursively(this, accumulator)
    val index = accumulator.indexOf(adapter).takeIf { it >= 0 } ?: return null
    return accumulator.getOrNull(index - 1) as? OptionalAdapter
}

fun concatConfig(
    isolateViewTypes: Boolean = ConcatAdapter.Config.DEFAULT.isolateViewTypes,
    stableIdMode: ConcatAdapter.Config.StableIdMode = ConcatAdapter.Config.DEFAULT.stableIdMode,
) = ConcatAdapter.Config
    .Builder()
    .setIsolateViewTypes(isolateViewTypes)
    .setStableIdMode(stableIdMode)
    .build()


inline fun <reified T : Any> CustomConcatAdapter.firstByType(): T {
    return delegate.firstByType()
}

inline fun <reified T : Any> CustomConcatAdapter.findByType(): T? {
    return delegate.findByType()
}

inline fun <reified T : Any> ConcatAdapter.firstByType(): T {
    return findByType()!!
}

inline fun <reified T : Any> ConcatAdapter.findByType(): T? {
    val result = mutableListOf<Adapter<*>> ()
    flattenAdaptersRecursively(this, result)
    return result.asSequence()
        .distinct()
        .filterIsInstance<T>()
        .firstOrNull()
}

fun flattenAdaptersRecursively(
    adapter: Adapter<*>,
    accumulator: MutableList<Adapter<*>>,
) {
    accumulator.add(adapter)

    when (adapter) {
        is ConcatAdapter -> {
            adapter.adapters.forEach { flattenAdaptersRecursively(it, accumulator) }
            accumulator.addAll(adapter.adapters)
        }
        is DelegateAdapter<*> -> {
            flattenAdaptersRecursively(adapter.delegate, accumulator)
            accumulator.add(adapter.delegate)
        }
        else -> {}
    }
}

fun CustomConcatAdapter.printAdapters(): String {
    val acc = mutableListOf<Pair<Adapter<*>, Int>>()
    printRecursively(this, acc, 0)
    return buildString {
        for ((adapter, level) in acc.distinctBy { it.first }) {
            repeat(level) {
                append('\t')
            }
            append(adapter)
            appendLine()
        }
    }
}

private fun printRecursively(
    adapter: Adapter<*>,
    accumulator: MutableList<Pair<Adapter<*>, Int>>,
    level: Int,
) {
    accumulator.add(adapter to level)

    when (adapter) {
        is ConcatAdapter -> {
            adapter.adapters.forEach { printRecursively(it, accumulator, level + 1) }
            accumulator.addAll(adapter.adapters.map { it to level + 1 })
        }
        is DelegateAdapter<*> -> {
            printRecursively(adapter.delegate, accumulator, level + 1)
            accumulator.add(adapter.delegate to level + 1)
        }
        else -> {}
    }
}