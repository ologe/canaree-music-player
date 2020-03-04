package dev.olog.shared.widgets.adaptive

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.core.math.MathUtils
import androidx.palette.graphics.Palette
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.palette.ColorUtil
import dev.olog.shared.android.palette.ImageProcessor
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.lazyFast
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class AdaptiveColorImageViewPresenter(
    private val context: Context
): CoroutineScope by MainScope() {

    private val isDarkMode by lazyFast {
        context.isDarkMode()
    }

    private val defaultProcessorColors = ValidProcessorColors(
        context.colorBackground(),
        context.textColorPrimary(),
        context.textColorSecondary()
    )

    private val defaultPaletteColors = ValidPaletteColors(context.colorAccent())

    private val processorPalettePublisher = ConflatedBroadcastChannel(defaultProcessorColors)
    private val palettePublisher = ConflatedBroadcastChannel(defaultPaletteColors)

    private var processorJob by autoDisposeJob()
    private var paletteJob by autoDisposeJob()

    fun observeProcessorColors(): Flow<ProcessorColors> = processorPalettePublisher
        .asFlow()

    fun observePalette(): Flow<PaletteColors> = palettePublisher.asFlow()

    fun onNextImage(drawable: Drawable?) {
        onNextImage(drawable?.toBitmap())
    }

    @SuppressLint("ConcreteDispatcherIssue")
    fun onNextImage(bitmap: Bitmap?) {

        if (bitmap == null) {
            processorPalettePublisher.offer(defaultProcessorColors)
            palettePublisher.offer(defaultPaletteColors)
            return
        }

        processorJob = launch(Dispatchers.Default) {
            val image = ImageProcessor(context).processImage(bitmap)
            yield()
            processorPalettePublisher.offer(
                ValidProcessorColors(
                    desaturate(image.background),
                    desaturate(image.primaryTextColor),
                    desaturate(image.secondaryTextColor)
                )
            )
        }

        paletteJob = launch(Dispatchers.Default) {
            val palette = Palette.from(bitmap)
                .maximumColorCount(24)
                .generate()
            yield()
            val accent = desaturate(ColorUtil.getAccentColor(context, palette))
            palettePublisher.offer(ValidPaletteColors(accent))
        }
    }

    private fun desaturate(color: Int): Int {
        if (!isDarkMode){
            return color
        }

        if (color == Color.TRANSPARENT) {
            // can't desaturate transparent color
            return color
        }
        val amount = .25f
        val minDesaturation = .75f

        val hsl = floatArrayOf(0f, 0f, 0f)
        ColorUtils.colorToHSL(color, hsl)
        if (hsl[1] > minDesaturation) {
            hsl[1] = MathUtils.clamp(
                hsl[1] - amount,
                minDesaturation - 0.1f,
                1f
            )
        }
        return ColorUtils.HSLToColor(hsl)
    }

}