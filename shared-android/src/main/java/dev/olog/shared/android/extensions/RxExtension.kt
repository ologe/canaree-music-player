@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import io.reactivex.disposables.Disposable

inline fun Disposable?.unsubscribe(){
    this?.let {
        if (!isDisposed){
            dispose()
        }
    }
}