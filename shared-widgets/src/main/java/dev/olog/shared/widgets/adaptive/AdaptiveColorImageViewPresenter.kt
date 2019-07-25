package dev.olog.shared.widgets.adaptive

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.palette.ColorUtil
import dev.olog.shared.android.palette.ImageProcessor
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class AdaptiveColorImageViewPresenter(
    private val context: Context
) {

    private val defaultProcessorColors =
        ValidProcessorColors(
            context.colorBackground(), context.textColorPrimary(), context.textColorSecondary()
        )

    private val defaultPaletteColors =
        ValidPaletteColors(context.colorAccent())

    private val processorPalettePublisher = ConflatedBroadcastChannel(defaultProcessorColors)
    private val palettePublisher = ConflatedBroadcastChannel(defaultPaletteColors)

    private var processorJob: Job? = null
    private var paletteJob: Job? = null

    fun observeProcessorColors(): Flow<ProcessorColors> = processorPalettePublisher
        .asFlow()

    fun observePalette(): Flow<PaletteColors> = palettePublisher.asFlow()

    fun onNextImage(drawable: Drawable?) {
        try {
            onNextImage(drawable?.toBitmap())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun onNextImage(bitmap: Bitmap?) {
        try {
            processorJob?.cancel()
            paletteJob?.cancel()

            if (bitmap == null) {
                processorPalettePublisher.offer(defaultProcessorColors)
                palettePublisher.offer(defaultPaletteColors)
                return
            }

            processorJob = GlobalScope.launch(Dispatchers.Default) {
                val image = ImageProcessor(context).processImage(bitmap)
                yield()
                processorPalettePublisher.offer(
                    ValidProcessorColors(image.background, image.primaryTextColor, image.secondaryTextColor)
                )
            }

            paletteJob = GlobalScope.launch(Dispatchers.Default) {
                val palette = Palette.from(bitmap)
                    .maximumColorCount(24)
                    .generate()
                yield()
                val accent = ColorUtil.getAccentColor(context, palette)
                palettePublisher.offer(ValidPaletteColors(accent))
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            processorPalettePublisher.offer(defaultProcessorColors)
            palettePublisher.offer(defaultPaletteColors)
        }
    }

}