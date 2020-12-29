package dev.olog.feature.base

import androidx.fragment.app.Fragment

interface RestorableScrollHelper {

    fun restoreUpperWidgetsTranslation()

}

fun Fragment.restoreUpperWidgetsTranslation() {
    (requireActivity() as RestorableScrollHelper).restoreUpperWidgetsTranslation()
}