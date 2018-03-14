package dev.olog.msc.presentation

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ErrorPublisher @Inject constructor(
        @ApplicationContext private val context: Context
) {

    private val noNetworkMessage = context.getString(R.string.common_no_internet)
    private val noResultsMessage = context.getString(R.string.common_no_results)
    private val publisher = PublishSubject.create<String>()

    fun observe(): Observable<String> = publisher

    fun noNetwork(){
        publisher.onNext(noNetworkMessage)
    }

    fun noResultsFound(){
        publisher.onNext(noResultsMessage)
    }

}