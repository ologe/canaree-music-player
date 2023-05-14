/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.olog.data.playlist.persister

import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import dev.olog.data.utils.extension

/**
 * see https://cs.android.com/android/platform/superproject/+/master:packages/providers/MediaProvider/src/com/android/providers/media/util/MimeUtils.java
 */
object PlaylistMimeUtils {

    private const val MIMETYPE_UNKNOWN = "application/octet-stream"

    fun resolve(file: DocumentFile): String {
        val extension = file.extension.takeIf { it?.isNotBlank() == true } ?: return MIMETYPE_UNKNOWN
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: return MIMETYPE_UNKNOWN
    }

}