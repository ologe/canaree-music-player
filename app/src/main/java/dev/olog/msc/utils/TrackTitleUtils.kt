package dev.olog.msc.utils

object TrackTitleUtils {

    fun adjust(title: String): TrackTitleResult {
        val builder = StringBuilder(title)

        var isExplicit = false
        var isRemix = false

        var startRound = builder.indexOf("(")
        var startSquare = builder.indexOf("[")
        var start: Int
        if (startRound > -1 && startSquare > -1) {
            start = Math.min(startRound, startSquare)
        } else if (startRound > -1) {
            start = startRound
        } else {
            start = startSquare
        }

        var endRound: Int
        var endSquare: Int
        var end: Int

        while (start > 0) {
            endRound = builder.indexOf(")", start) + 1
            endSquare = builder.indexOf("]", start) + 1
            if (endRound > start && endSquare > start) {
                end = Math.min(endRound, endSquare)
            } else if (endRound > start) {
                end = endRound
            } else {
                end = endSquare
            }

            if (end > start) {

                val substring = builder.toString().toLowerCase().substring(start, end)

                val canDelete = substring.contains("official") || substring.contains("lyrics") ||
                        substring.contains("audio") || substring.contains("video") || substring.contains("hd")

                if (canDelete) {
                    builder.replace(start, end, "")

                } else if (substring.contains("explicit")) {
                    builder.replace(start, end, "")
                    isExplicit = true
                } else if (substring.contains("remix")) {
                    builder.replace(start, end, "")
                    isRemix = true
                } else {
                    start = end
                }

                startRound = builder.indexOf("(", start)
                startSquare = builder.indexOf("[", start)
                if (startRound > start && startSquare > start) {
                    start = Math.min(startRound, startSquare)
                } else if (startRound > start) {
                    start = startRound
                } else {
                    start = startSquare
                }
            } else {
                start = -1
            }
        }

        return TrackTitleResult(builder.toString().trim(), isExplicit, isRemix)
    }

}

data class TrackTitleResult(
        val title: String,
        val explicit: Boolean,
        val remix: Boolean
)