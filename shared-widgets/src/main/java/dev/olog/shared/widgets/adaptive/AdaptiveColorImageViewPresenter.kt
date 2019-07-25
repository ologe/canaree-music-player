package dev.olog.shared.widgets.adaptive

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.palette.ColorUtil
import dev.olog.shared.android.palette.ImageProcessor
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class AdaptiveColorImageViewPresenter(
    private val context: Context
) {

    private val defaultProcessorColors =
        ValidProcessorColors(
            context.colorBackground(), context.textColorPrimary(), context.textColorSecondary()
        )

    private val defaultPaletteColors =
        ValidPaletteColors(context.colorAccent())

    private val processorPalettePublisher =
        BehaviorSubject.createDefault<ProcessorColors>(defaultProcessorColors)
    private val palettePublisher =
        BehaviorSubject.createDefault<PaletteColors>(defaultPaletteColors)

    private var processorDisposable: Disposable? = null
    private var paletteDisposable: Disposable? = null

    fun observeProcessorColors(): Observable<ProcessorColors> = processorPalettePublisher
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.computation())
        .debounce(200, TimeUnit.MILLISECONDS)

    fun observePalette(): Observable<PaletteColors> = palettePublisher
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.computation())
        .debounce(200, TimeUnit.MILLISECONDS)

    fun onNextImage(drawable: Drawable?) {
        try {
            onNextImage(drawable?.toBitmap())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun onNextImage(bitmap: Bitmap?) {
        try {
            processorDisposable.unsubscribe()
            paletteDisposable.unsubscribe()

            if (bitmap == null) {
                processorPalettePublisher.onNext(defaultProcessorColors)
                palettePublisher.onNext(defaultPaletteColors)
                return
            }

            processorDisposable =
                Single.fromCallable { ImageProcessor(context).processImage(bitmap) }
                    .subscribeOn(Schedulers.computation())
                    .subscribe({
                        processorPalettePublisher.onNext(
                            ValidProcessorColors(
                                it.background,
                                it.primaryTextColor, it.secondaryTextColor
                            )
                        )
                    }, Throwable::printStackTrace)

            paletteDisposable = Single.fromCallable {
                Palette.from(bitmap)
                    .maximumColorCount(24)
                    .generate()
            }
                .map { ColorUtil.getAccentColor(context, it) }
                .subscribeOn(Schedulers.computation())
                .subscribe({
                    palettePublisher.onNext(
                        ValidPaletteColors(
                            it
                        )
                    )
                }, Throwable::printStackTrace)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}