package dev.olog.lib.audio.tagger

import dev.olog.lib.audio.tagger.model.Tags
import java.io.File

interface AudioTagger {

    fun canBeHandled(file: File): Boolean

    fun read(file: File): Tags

    fun save(file: File, tags: Tags)

}