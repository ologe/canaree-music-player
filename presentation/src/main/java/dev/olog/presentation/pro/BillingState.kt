package dev.olog.presentation.pro

class BillingState(
    val isTrial: Boolean,
    val isBought: Boolean
) {

    fun isPremiumEnabled(): Boolean {
        return isTrial || isBought
    }

    fun isPremiumStrict() = isBought && !isTrial

}
