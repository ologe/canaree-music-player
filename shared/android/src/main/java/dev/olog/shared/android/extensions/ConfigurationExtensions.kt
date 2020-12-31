package dev.olog.shared.android.extensions

import android.content.res.Configuration

inline val Configuration.isDarkMode: Boolean
    get() = uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES