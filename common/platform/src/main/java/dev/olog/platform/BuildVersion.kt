package dev.olog.platform

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@Suppress("unused")
object BuildVersion {

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.LOLLIPOP_MR1) // 22
    fun isLollipopMR1(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.M) // 23
    fun isMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N) // 24
    fun isNougat(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N_MR1) // 25
    fun isNougatMR1(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O) // 26
    fun isOreo(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1) // 27
    fun isOreoMR1(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P) // 28
    fun isPie(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q) // 29
    fun isQ(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R) // 30
    fun isR(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S) // 31
    fun isS(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S_V2) // 32
    fun isSv2(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU) // 33
    fun isTiramisu(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

}