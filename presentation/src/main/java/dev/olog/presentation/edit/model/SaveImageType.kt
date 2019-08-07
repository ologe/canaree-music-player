package dev.olog.presentation.edit.model

import android.graphics.Bitmap

sealed class SaveImageType {
    object Skip: SaveImageType()
    object Original: SaveImageType()
    class Stylized(val bitmap: Bitmap): SaveImageType()
}