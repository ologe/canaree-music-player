package dev.olog.presentation.edit

import java.io.InputStream

sealed class ImageType {
    class String(val url: kotlin.String) : ImageType()
    class Stream(val stream: InputStream?) : ImageType()
}