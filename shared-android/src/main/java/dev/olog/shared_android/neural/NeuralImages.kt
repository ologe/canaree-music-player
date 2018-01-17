package dev.olog.shared_android.neural

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import org.tensorflow.contrib.android.TensorFlowInferenceInterface

object NeuralImages {

    const val NUM_STYLES = 26

    private const val desiredSize = 1024
    private const val MODEL_FILE = "file:///android_asset/stylize_quantized.pb"
    private val INPUT_NODE = "input"
    private val STYLE_NODE = "style_num"
    private val OUTPUT_NODE = "transformer/expand/conv3/conv/Sigmoid"

    private var styleVals = FloatArray(NUM_STYLES)

    fun setStyle(stylePosition: Int){
        styleVals.forEachIndexed { index, _ ->
            styleVals[index] = if (index == stylePosition) 1f else 0f
        }
    }

    fun getThumbnail(position: Int): Uri {
        return Uri.parse("file:///android_asset/thumbnails/style$position.webp")
    }

    fun stylizeTensorFlow(context: Context, bitmap: Bitmap): Bitmap {
        val inferenceInterface = TensorFlowInferenceInterface(context.assets, MODEL_FILE)

        val intValues = IntArray(desiredSize * desiredSize)
        val floatValues = FloatArray(desiredSize * desiredSize * 3)

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredSize, desiredSize, false)

        scaledBitmap.getPixels(intValues, 0, scaledBitmap.width, 0, 0, scaledBitmap.width, scaledBitmap.height)

        for (i in intValues.indices) {
            val `val` = intValues[i]
            floatValues[i * 3] = (`val` shr 16 and 0xFF) / 255.0f
            floatValues[i * 3 + 1] = (`val` shr 8 and 0xFF) / 255.0f
            floatValues[i * 3 + 2] = (`val` and 0xFF) / 255.0f
        }

        // TODO: Process the image in TensorFlow here.

        // Copy the input data into TensorFlow.
        inferenceInterface.feed(INPUT_NODE, floatValues,
                1, scaledBitmap.width.toLong(), scaledBitmap.height.toLong(), 3)
        inferenceInterface.feed(STYLE_NODE, styleVals, NUM_STYLES.toLong())

        // Execute the output node's dependency sub-graph.
        inferenceInterface.run(arrayOf(OUTPUT_NODE), false)

        // Copy the data from TensorFlow back into our array.
        inferenceInterface.fetch(OUTPUT_NODE, floatValues)

        for (i in intValues.indices) {
            intValues[i] = (-0x1000000
                    or ((floatValues[i * 3] * 255).toInt() shl 16)
                    or ((floatValues[i * 3 + 1] * 255).toInt() shl 8)
                    or (floatValues[i * 3 + 2] * 255).toInt())
        }

        scaledBitmap.setPixels(intValues, 0, scaledBitmap.width, 0, 0, scaledBitmap.width, scaledBitmap.height)

        return scaledBitmap
    }

}