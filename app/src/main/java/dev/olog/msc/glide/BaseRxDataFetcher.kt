package dev.olog.msc.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.data.HttpUrlFetcher
import com.bumptech.glide.load.model.GlideUrl
import dev.olog.msc.utils.k.extension.defer
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Singles
import java.io.InputStream
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

abstract class BaseRxDataFetcher : DataFetcher<InputStream> {

    companion object {
        private const val TIMEOUT = 2500

        private var counter = AtomicLong(1)
        // limit 5 request per second
        private const val THRESHOLD = 300L
    }

    protected var disposable: Disposable? = null

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun getDataSource(): DataSource = DataSource.REMOTE

    override fun cleanup() {
        disposable.unsubscribe()
    }

    override fun cancel() {
        disposable.unsubscribe()
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        disposable = shouldFetch()
                .flatMap { should ->
                    val single = if (should){
                        delay()
                    } else Single.just(false)
                    // DO NOT DELETE DEFER
                    single.flatMap { execute(priority, callback).defer() }
                }.subscribe({ image ->
                    if (image.isNotBlank()){
                        val urlFetcher = HttpUrlFetcher(GlideUrl(image), TIMEOUT)
                        urlFetcher.loadData(priority, callback)
                    } else {
                        callback.onLoadFailed(NoSuchElementException())
                    }
                }, {
                    it.printStackTrace()
                    callback.onLoadFailed(it as Exception)
                })
    }

    private fun delay(): Single<*>{
        val current = counter.incrementAndGet()

        return Singles.zip(
                Observable.timer(current * THRESHOLD, TimeUnit.MILLISECONDS).firstOrError(),
                Single.just(false), { _, _ -> false })
                .doOnEvent { _, _ -> counter.decrementAndGet() }
    }

    protected abstract fun execute(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>)
            : Single<String>

    protected abstract fun shouldFetch(): Single<Boolean>

}