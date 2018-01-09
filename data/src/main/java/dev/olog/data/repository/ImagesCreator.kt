package dev.olog.data.repository

import dev.olog.shared.unsubscribe
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class ImagesCreator @Inject constructor(){

    private val creatingImages = AtomicBoolean(false)

    private var imageDisposable : Disposable? = null

    fun subscribe(createImages: Single<Any>){
        if (creatingImages.compareAndSet(false, true)){
            imageDisposable.unsubscribe()
            imageDisposable = createImages.subscribe({
                creatingImages.compareAndSet(true, false)
            }, Throwable::printStackTrace)
        }
    }

    fun unsubscribe(){
        creatingImages.compareAndSet(true, false)
        imageDisposable.unsubscribe()
    }

}