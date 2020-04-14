package dev.olog.feature.presentation.base.widget.adaptive

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import dev.olog.core.coroutines.viewScope
import dev.olog.lib.ColorDesaturationUtils
import dev.olog.shared.android.dark.mode.isDarkMode
import dev.olog.shared.android.extensions.colorAccent
import dev.olog.shared.android.extensions.colorBackground
import dev.olog.shared.android.extensions.textColorPrimary
import dev.olog.shared.android.extensions.textColorSecondary
import dev.olog.shared.coroutines.autoDisposeJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.yield
import java.lang.ref.WeakReference

class AdaptiveColorImageViewPresenter(
    view: View
) {

    private val context = view.context
    private val view = WeakReference(view)

    private val isDarkMode = context.isDarkMode()

    private val defaultProcessorColors =
        ValidProcessorColors(
            context.colorBackground(),
            context.textColorPrimary(),
            context.textColorSecondary()
        )

    private val defaultPaletteColors =
        ValidPaletteColors(
            context.colorAccent()
        )

    private val processorPalettePublisher = ConflatedBroadcastChannel(defaultProcessorColors)
    private val palettePublisher = ConflatedBroadcastChannel(defaultPaletteColors)

    private var processorJob by autoDisposeJob()
    private var paletteJob by autoDisposeJob()

    fun observeProcessorColors(): Flow<ProcessorColors> = processorPalettePublisher
        .asFlow()

    fun observePaletteColors(): Flow<PaletteColors> = palettePublisher.asFlow()

    fun onNextImage(drawable: Drawable?) {
        onNextImage(drawable?.toBitmap())
    }

    @SuppressLint("ConcreteDispatcherIssue")
    fun onNextImage(bitmap: Bitmap?) {
        val view = view.get() ?: return


        if (bitmap == null) {
            processorPalettePublisher.offer(defaultProcessorColors)
            palettePublisher.offer(defaultPaletteColors)
            return
        }

        processorJob = view.viewScope.launchWhenAttached(Dispatchers.Default) {
            val image = dev.olog.feature.presentation.base.palette.ImageProcessor(
                context
            ).processImage(bitmap)
            yield()
            processorPalettePublisher.offer(
                ValidProcessorColors(
                    desaturate(image.background),
                    desaturate(image.primaryTextColor),
                    desaturate(image.secondaryTextColor)
                )
            )
        }

        paletteJob = view.viewScope.launchWhenAttached(Dispatchers.Default) {
            val palette = Palette.from(bitmap)
                .maximumColorCount(24)
                .generate()
            yield()
            val accent = desaturate(dev.olog.feature.presentation.base.palette.ColorUtil.getAccentColor(context, palette))
            palettePublisher.offer(
                ValidPaletteColors(
                    accent
                )
            )
        }
    }

    private fun desaturate(color: Int): Int {
        if (!isDarkMode){
            return color
        }
        return ColorDesaturationUtils.desaturate(color, .25f, .75f)
    }

}