package dev.olog.data.repository.lastfm

import android.provider.MediaStore
import java.util.*

object QuerySanitizer {

    private val FEATURING_REGEX = "(?i)(featuring|feat|ft)\\.?".toRegex()
    // skip dot, there are artists like b.o.b
    // skip ', there are tracks like let's
    // http://www.asciitable.com/
    private val SPECIAL_CHARS_REGEX = "[\\x21-\\x26\\x28-\\x2d\\x3a-\\x40\\x5b-\\x60\\x7b-\\x7e]".toRegex()
    private val WORDS_TO_FILTER = setOf(
        "official",
        "lyrics",
        "audio",
        "music",
        "video",
        "lyric",
        "lyrics",
        "version",
        "explicit",
        "remix",
        "remastered",
        "freestyle",
        "cover",
        "album",
        "extended",
        // spanish
        "vídeo",
        "oficial",
        "letra"
    )

    private val MULTIPLE_WORDS_REGEX = buildString {
        append("(?:")
        append(WORDS_TO_FILTER.joinToString("|") { "$it\\x20?()" })
        append("){2,}")
    }.toRegex()

    private val WORDS_WITHIN_PARENTHESIS_REGEX = buildString {
        append("(?:\\(()|\\[()|\\{()){1}\\s*(?:")
        append(WORDS_TO_FILTER.joinToString("|") { "$it()" })
        append(")\\s*(?:\\)()|\\]()|\\}()){1}")
    }.toRegex()

    private val MULTIPLE_SPACES_REGEX = "\\s{2,}".toRegex()

    fun sanitize(original: String): String {
        if (original == MediaStore.UNKNOWN_STRING) {
            return ""
        }
        return original
            .trim()
            .lowercase(Locale.getDefault())
            .replace(MULTIPLE_SPACES_REGEX, " ") // removes multiple spaces
            .replace(FEATURING_REGEX, " ") // removes featuring
            .replace("$", "s")
            .replace("@", "a")
            .replace(WORDS_WITHIN_PARENTHESIS_REGEX, " ") // matches single illegal word within parenthesis
            .replace(SPECIAL_CHARS_REGEX, " ") // matches more than 2 illegal words
            .replace(MULTIPLE_WORDS_REGEX, " ") // removed 'official audio/video'
            .replace(MULTIPLE_SPACES_REGEX, " ") // removes multiple spaces
    }

}