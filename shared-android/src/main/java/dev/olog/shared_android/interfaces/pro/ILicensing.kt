package dev.olog.shared_android.interfaces.pro

import com.google.android.vending.licensing.LicenseCheckerCallback

interface ILicensing {

    fun check(listener: LicenseCheckerCallback)

}