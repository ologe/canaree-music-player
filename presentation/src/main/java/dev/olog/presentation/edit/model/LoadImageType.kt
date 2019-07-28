package dev.olog.presentation.edit.model

import java.io.InputStream

sealed class LoadImageType {
    class String(val url: kotlin.String) : LoadImageType()
    class Stream(val stream: InputStream?) : LoadImageType()
}