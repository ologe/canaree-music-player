package dev.olog.msc.presentation.utils.images

import android.graphics.Bitmap

data class ImageProcessorResult(
        val bitmap: Bitmap,
        val background: Int,
        val primaryTextColor: Int,
        val secondaryTextColor: Int
)