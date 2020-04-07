package dev.olog.shared.android.extensions

import androidx.fragment.app.Fragment
import dev.olog.shared.android.theme.ThemeManager
import dev.olog.shared.android.theme.themeManager

val Fragment.themeManager: ThemeManager
    get() = requireContext().themeManager