package dev.olog.feature.stylize

import ai.fritz.core.Fritz
import ai.fritz.core.FritzOnDeviceModel
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
import dev.olog.core.entity.ImageStyle
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

    override suspend fun stylize(imageStyle: ImageStyle, bitmap: Bitmap): Bitmap {
        predictor = FritzVision.StyleTransfer.getPredictor(imageStyle.toFritzStyle()).apply {
            setOptions(options)
        }
        yield()
        return withContext(Dispatchers.IO) {
            val fritzImage = FritzVisionImage.fromBitmap(bitmap)
            predictor!!.predict(fritzImage).toBitmap(Size(bitmap.width, bitmap.height))
        }
    }

    private fun ImageStyle.toFritzStyle(): FritzOnDeviceModel = when (this) {
        ImageStyle.BICENTENNIAL_PRINT -> PaintingStyles.BICENTENNIAL_PRINT
        ImageStyle.HEAD_OF_CLOWN -> PaintingStyles.HEAD_OF_CLOWN
        ImageStyle.HORSES_ON_SEASHORE -> PaintingStyles.HORSES_ON_SEASHORE
        ImageStyle.FEMMES -> PaintingStyles.FEMMES
        ImageStyle.POPPY_FIELD -> PaintingStyles.POPPY_FIELD
        ImageStyle.RITMO_PLASTICO -> PaintingStyles.RITMO_PLASTICO
        ImageStyle.STARRY_NIGHT -> PaintingStyles.STARRY_NIGHT
        ImageStyle.THE_SCREAM -> PaintingStyles.THE_SCREAM
        ImageStyle.THE_TRAIL -> PaintingStyles.THE_TRAIL
    }

}