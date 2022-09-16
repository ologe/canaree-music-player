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
import org.xmlpull.v1.XmlPullParser.END_DOCUMENT
import org.xmlpull.v1.XmlPullParser.START_TAG
import org.xmlpull.v1.XmlPullParserException
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Custom implementation of https://cs.android.com/android/platform/superproject/+/master:packages/providers/MediaProvider/src/com/android/providers/media/playlist/WplPlaylistPersister.java
 */
class WplPlaylistPersister : PlaylistPersister {

    companion object {
        private const val TAG_SMIL = "smil"
        private const val TAG_BODY = "body"
        private const val TAG_SEQ = "seq"
        private const val TAG_MEDIA = "media"
        private const val ATTR_SRC = "src"
    }

    override fun read(file: File): List<String> {
        try {
            val parser = Xml.newPullParser()
            parser.setInput(file.inputStream(), StandardCharsets.UTF_8.name())

            val result = mutableListOf<String>()

            var type: Int
            while (parser.next().also { type = it } != END_DOCUMENT) {
                if (type != START_TAG) continue
                if (TAG_MEDIA == parser.name) {
                    val src = parser.getAttributeValue(null, ATTR_SRC) ?: continue
                    result.add(src.replace('\\', '/'))
                }
            }

            return result
        } catch (ex: XmlPullParserException) {
            ex.printStackTrace()
            return emptyList()
        }
    }

    override fun write(file: File, items: List<String>) {
        val doc = Xml.newSerializer()
        doc.setOutput(file.outputStream(), StandardCharsets.UTF_8.name())
        doc.startDocument(null, true)
        doc.startTag(null, TAG_SMIL)
        doc.startTag(null, TAG_BODY)
        doc.startTag(null, TAG_SEQ)
        for (item in items) {
            doc.startTag(null, TAG_MEDIA)
            doc.attribute(null, ATTR_SRC, item)
            doc.endTag(null, TAG_MEDIA)
        }
        doc.endTag(null, TAG_SEQ)
        doc.endTag(null, TAG_BODY)
        doc.endTag(null, TAG_SMIL)
        doc.endDocument()
    }
}