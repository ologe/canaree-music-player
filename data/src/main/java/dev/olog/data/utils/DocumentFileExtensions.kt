package dev.olog.data.utils

import androidx.documentfile.provider.DocumentFile

val DocumentFile.extension: String?
    get() = name?.substringAfterLast('.', "")