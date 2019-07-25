package dev.olog.feature.stylize

import ai.fritz.core.Fritz
import ai.fritz.fritzvisionstylepaintings.PaintingStyles
import ai.fritz.vision.FritzVision
import ai.fritz.vision.FritzVisionCropAndScale
import ai.fritz.vision.FritzVisionImage
import ai.fritz.vision.styletransfer.FritzVisionStylePredictor
import ai.fritz.vision.styletransfer.FritzVisionStylePredictorOptions
import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import androidx.annotation.Keep
import dev.olog.core.Stylizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

// used by reflection
@Suppress("unused")
@Keep
class StylizerImpl(context: Context) : Stylizer {

    private var predictor: FritzVisionStylePredictor? = null

    private val options = FritzVisionStylePredictorOptions.Builder()
        .cropAndScaleOption(FritzVisionCropAndScale.CENTER_CROP)
        .build()

    init {
        Fritz.configure(context, BuildConfig.FRITZ_KEY)


    }

    override suspend fun stylize(bitmap: Bitmap): Bitmap {
        predictor = FritzVision.StyleTransfer.getPredictor(PaintingStyles.THE_TRAIL).apply {
            setOptions(options)
        }
        yield()
        return withContext(Dispatchers.IO){
            val fritzImage = FritzVisionImage.fromBitmap(bitmap)
            predictor!!.predict(fritzImage).toBitmap(Size(bitmap.width, bitmap.height))
        }
    }
}