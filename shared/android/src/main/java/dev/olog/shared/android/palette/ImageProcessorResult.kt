package dev.olog.shared.android.palette

import android.graphics.Bitmap

data class ImageProcessorResult(
    val bitmap: Bitmap,
    val background: Int,
    val primaryTextColor: Int,
    val secondaryTextColor: Int
)