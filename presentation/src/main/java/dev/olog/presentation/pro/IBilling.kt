package dev.olog.presentation.pro

import io.reactivex.Observable

interface IBilling {

    fun observeBillingsState(): Observable<BillingState>
    fun getBillingsState(): BillingState
    fun purchasePremium()

}

