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

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.File
import java.nio.charset.StandardCharsets


/**
 * Custom implementation of https://cs.android.com/android/platform/superproject/+/master:packages/providers/MediaProvider/src/com/android/providers/media/playlist/XspfPlaylistPersister.java
 */
class XspfPlaylistPersister : PlaylistPersister {

    companion object {
        private const val TAG_PLAYLIST = "playlist"
        private const val TAG_TRACK_LIST = "trackList"
        private const val TAG_TRACK = "track"
        private const val TAG_LOCATION = "location"
    }

    override fun read(file: File): List<String> {
        try {
            val parser = Xml.newPullParser()
            parser.setInput(file.inputStream(), StandardCharsets.UTF_8.name())

            val result = mutableListOf<String>()

            var type: Int
            while (parser.next().also { type = it } != XmlPullParser.END_DOCUMENT) {
                if (type != XmlPullParser.START_TAG) continue
                if (parser.name == TAG_LOCATION) {
                    val src = parser.nextText() ?: continue
                    result.add(src.replace('\\', '/'))
                }
            }

            return result
        } catch (ex: XmlPullParserException) {
            return emptyList()
        }
    }

    override fun write(file: File, items: List<String>) {
        val doc = Xml.newSerializer()
        doc.setOutput(file.outputStream(), StandardCharsets.UTF_8.name())
        doc.startDocument(null, true)
        doc.startTag(null, TAG_PLAYLIST)
        doc.startTag(null, TAG_TRACK_LIST)
        for (item in items) {
            doc.startTag(null, TAG_TRACK)
            doc.startTag(null, TAG_LOCATION)
            doc.text(item)
            doc.endTag(null, TAG_LOCATION)
            doc.endTag(null, TAG_TRACK)
        }
        doc.endTag(null, TAG_TRACK_LIST)
        doc.endTag(null, TAG_PLAYLIST)
        doc.endDocument()
    }
}