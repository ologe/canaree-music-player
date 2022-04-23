@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.LOLLIPOP_MR1)
inline fun isLollipop_MR_1(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.M)
inline fun isMarshmallow(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
inline fun isNougat(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N_MR1)
inline fun isNougat_MR1(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
inline fun isOreo(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
inline fun isOreo_MR1(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
inline fun isP(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
inline fun isQ(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}