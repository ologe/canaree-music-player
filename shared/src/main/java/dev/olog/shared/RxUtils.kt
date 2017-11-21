package dev.olog.shared

import io.reactivex.disposables.Disposable

fun Disposable?.unsubscribe(){
    this?.let {
        if (!isDisposed){
            dispose()
        }
    }
}