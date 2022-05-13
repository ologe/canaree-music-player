package dev.olog.feature.lyrics.offline

import android.widget.TextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat

object OffsetCalculator {

    fun compute(textView: TextView, lyrics: CharSequence, currentParagraph: Int): Int {
        val textParams = TextViewCompat.getTextMetricsParams(textView)
        val precomputedText = PrecomputedTextCompat.create(lyrics, textParams)
        val proportion = currentParagraph.toFloat() / precomputedText.paragraphCount.toFloat()
        return (textView.height.toFloat() * proportion).toInt()
    }

}

