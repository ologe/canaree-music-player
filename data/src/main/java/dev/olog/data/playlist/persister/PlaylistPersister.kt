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

import dev.olog.shared.android.extensions.resolveMimeType
import java.io.File
import java.util.*

/**
 * Custom implementation of https://cs.android.com/android/platform/superproject/+/master:packages/providers/MediaProvider/src/com/android/providers/media/playlist/PlaylistPersister.java
 *
 * Interface that knows how to {@link #read} and {@link #write} a set of
 * playlist items using a specific file format. This design allows you to easily
 * convert between playlist file formats by reading one format and writing to
 * another.
 */
interface PlaylistPersister {

    companion object {
        fun resolvePersister(file: File): PlaylistPersister? {
            return resolvePersister(file.resolveMimeType())
        }

        fun resolvePersister(mimeType: String): PlaylistPersister? {
            return when (mimeType.lowercase(Locale.ROOT)) {
                "audio/mpegurl",
                "audio/x-mpegurl",
                "application/vnd.apple.mpegurl",
                "application/x-mpegurl" -> M3uPlaylistPersister()
                "audio/x-scpls" -> PlsPlaylistPersister()
                "application/vnd.ms-wpl",
                "video/x-ms-asf" -> WplPlaylistPersister()
                "application/xspf+xml" -> XspfPlaylistPersister()
                else -> null
            }
        }
    }

    fun read(file: File): List<String>
    fun write(file: File, items: List<String>)

}