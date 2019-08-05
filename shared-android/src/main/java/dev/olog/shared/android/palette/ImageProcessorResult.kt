package dev.olog.shared.android.palette

import android.graphics.Bitmap

class ImageProcessorResult(
    @JvmField
    val bitmap: Bitmap,
    @JvmField
    val background: Int,
    @JvmField
    val primaryTextColor: Int,
    @JvmField
    val secondaryTextColor: Int
)