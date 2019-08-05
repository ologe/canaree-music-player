package dev.olog.presentation.pro

class BillingState(
    @JvmField
    val isTrial: Boolean,
    @JvmField
    val isBought: Boolean,
    @JvmField
    val canShowAd: Boolean
) {

    fun isPremiumEnabled(): Boolean {
        return isTrial || isBought || canShowAd
    }

    fun isPremiumStrict() = isBought


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BillingState

        if (isTrial != other.isTrial) return false
        if (isBought != other.isBought) return false
        if (canShowAd != other.canShowAd) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isTrial.hashCode()
        result = 31 * result + isBought.hashCode()
        result = 31 * result + canShowAd.hashCode()
        return result
    }


}
