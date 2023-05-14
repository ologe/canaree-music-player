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

import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter

/**
 * https://cs.android.com/android/platform/superproject/+/master:packages/providers/MediaProvider/src/com/android/providers/media/playlist/M3uPlaylistPersister.java
 */
class M3uPlaylistPersister : PlaylistPersister {

    override fun read(
        stream: InputStream
    ): List<String> = stream.bufferedReader().use { reader ->
        return buildList {
            for (line in reader.readLines()) {
                if (line.isNotBlank() && !line.startsWith("#")) {
                    this += line.replace("\\", "/")
                }
            }
        }
    }

    override fun write(
        stream: OutputStream,
        items: List<String>
    ): Unit = PrintWriter(stream).use { writer ->
        writer.println("#EXTM3U")
        for (item in items) {
            writer.println(item)
        }
    }
}