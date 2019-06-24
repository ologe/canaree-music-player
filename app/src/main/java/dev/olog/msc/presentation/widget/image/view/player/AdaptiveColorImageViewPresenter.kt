package dev.olog.msc.presentation.widget.image.view.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.crashlytics.android.Crashlytics
import dev.olog.msc.presentation.utils.images.ColorUtil
import dev.olog.msc.presentation.utils.images.ImageProcessor
import dev.olog.shared.extensions.*
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

internal class AdaptiveColorImageViewPresenter(
        private val context: Context

) {

    private val defaultProcessorColors = ValidProcessorColors(
            context.windowBackground(), context.textColorPrimary(), context.textColorSecondary())

    private val defaultPaletteColors = ValidPaletteColors(context.colorAccent())

    private val processorPalettePublisher = BehaviorSubject.createDefault<ProcessorColors>(defaultProcessorColors)
    private val palettePublisher = BehaviorSubject.createDefault<PaletteColors>(defaultPaletteColors)

    private var processorDisposable: Disposable? = null
    private var paletteDisposable: Disposable? = null

    fun observeProcessorColors() = processorPalettePublisher
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .debounceFirst()

    fun observePalette() = palettePublisher
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .debounceFirst()

    fun onNextImage(drawable: Drawable?){
        try {
            onNextImage(drawable?.toBitmap(300, 300))
        } catch (ex: Exception) {
            if (ex !is IllegalArgumentException){
                Crashlytics.logException(ex)
            }
        }
    }

    fun onNextImage(bitmap: Bitmap?){
        try {
            processorDisposable.unsubscribe()
            paletteDisposable.unsubscribe()

            if (bitmap == null){
                processorPalettePublisher.onNext(defaultProcessorColors)
                palettePublisher.onNext(defaultPaletteColors)
                return
            }

            processorDisposable = Single.fromCallable { ImageProcessor(context).processImage(bitmap) }
                    .subscribeOn(Schedulers.computation())
                    .subscribe({
                        processorPalettePublisher.onNext(ValidProcessorColors(it.background,
                                it.primaryTextColor, it.secondaryTextColor))
                    }, Throwable::printStackTrace)

            paletteDisposable = Single.fromCallable { Palette.from(bitmap).generate() }
                    .map { ColorUtil.getAccentColor(context, it) }
                    .subscribeOn(Schedulers.computation())
                    .subscribe({
                        palettePublisher.onNext(ValidPaletteColors(it))
                    }, Throwable::printStackTrace)
        } catch (ex: Exception){
            if (ex !is IllegalArgumentException){
                Crashlytics.logException(ex)
            }
        }
    }

}