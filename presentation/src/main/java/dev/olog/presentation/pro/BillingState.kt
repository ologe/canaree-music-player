package dev.olog.presentation.pro

data class BillingState(
    val isTrial: Boolean,
    val isBought: Boolean,
    val canShowAd: Boolean
) {

    fun isPremiumEnabled(): Boolean {
        return isTrial || isBought || canShowAd
    }

    fun isPremiumStrict() = isBought

}
