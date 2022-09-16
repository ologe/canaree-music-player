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

import java.io.File
import java.util.regex.Pattern

/**
 * Custom implementation of https://cs.android.com/android/platform/superproject/+/master:packages/providers/MediaProvider/src/com/android/providers/media/playlist/PlsPlaylistPersister.java
 */
class PlsPlaylistPersister : PlaylistPersister {

    companion object {
        private val PATTERN_PLS = Pattern.compile("File(\\d+)=(.+)")
    }

    override fun read(file: File): List<String> {
        val result = mutableListOf<Pair<Int, String>>()

        for (line in file.readLines()) {
            val matcher = PATTERN_PLS.matcher(line)
            if (matcher.matches()) {
                val index = matcher.group(1)!!.toInt()
                val item = matcher.group(2)!!.replace('\\', '/')
                result.add(index to item)
            }
        }

        return result.sortedBy {it.first}
            .map { it.second }
    }

    override fun write(file: File, items: List<String>) {
        file.printWriter().use {
            it.println("[playlist]")
            for ((i, item) in items.withIndex()) {
                it.println("File${i + 1}=$item")
            }
            it.println("NumberOfEntries=${items.size}")
            it.println("Version=2")
        }
    }
}