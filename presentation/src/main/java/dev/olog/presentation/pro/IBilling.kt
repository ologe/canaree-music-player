package dev.olog.presentation.pro

import io.reactivex.Observable

interface IBilling {

    fun isTrial(): Boolean
    fun isPremium(): Boolean
    fun isOnlyPremium(): Boolean
    fun observeIsPremium(): Observable<Boolean>
    fun observeTrialPremiumState(): Observable<BillingState>
    fun purchasePremium()

}

