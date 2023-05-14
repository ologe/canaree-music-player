/*
 * Copyright (C) 2020 The Android Open Source Project
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

import androidx.documentfile.provider.DocumentFile
import java.io.InputStream
import java.io.OutputStream

/**
 * https://cs.android.com/android/platform/superproject/+/master:packages/providers/MediaProvider/src/com/android/providers/media/playlist/PlaylistPersister.java
 */
interface PlaylistPersister {

    companion object {

        fun resolvePersister(file: DocumentFile): PlaylistPersister {
            return resolvePersister(PlaylistMimeUtils.resolve(file))
        }

        private fun resolvePersister(mimeType: String): PlaylistPersister = when (mimeType.lowercase()) {
            "audio/mpegurl",
            "audio/x-mpegurl",
            "application/vnd.apple.mpegurl",
            "application/x-mpegurl" -> M3uPlaylistPersister()
            "audio/x-scpls" -> PlsPlaylistPersister()
            "application/vnd.ms-wpl",
            "video/x-ms-asf" -> WplPlaylistPersister()
            "application/xspf+xml" -> XspfPlaylistPersister()
            else -> error("unsupported playlist format $mimeType")
        }

    }

    // TODO ensure all stream are correct closed
    fun read(stream: InputStream): List<String>
    fun write(stream: OutputStream, items: List<String>)

}