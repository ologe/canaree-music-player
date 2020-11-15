package dev.olog.shared.android.extensions

import androidx.lifecycle.SavedStateHandle

inline fun <T : Any?> SavedStateHandle.argument(
    key: String,
    crossinline initializer: (T) -> T = { it },
): T {
    return argument<T, T>(key, initializer)
}

@JvmName("argument2")
inline fun <T : Any?, R : Any?> SavedStateHandle.argument(
    key: String,
    crossinline initializer: (T) -> R,
): R {
    val argument = get<T>(key)!!
    return initializer(argument)
}