package dev.olog.presentation.edit.model

import android.graphics.Bitmap

sealed class SaveImageType {
    object NotSet: SaveImageType()
    object Original: SaveImageType()
    class Url(val url: String): SaveImageType()
    class Stylized(val bitmap: Bitmap): SaveImageType()
}