package dev.olog.shared

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.LOLLIPOP_MR1)
fun isLollipop_MR_1(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.M)
fun isMarshmallow(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
fun isNougat(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N_MR1)
fun isNougat_MR1(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
fun isOreo(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
fun isOreo_MR1(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
fun isP(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isQ(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}