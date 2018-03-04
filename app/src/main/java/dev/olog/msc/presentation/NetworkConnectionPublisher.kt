package dev.olog.msc.presentation

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class NetworkConnectionPublisher @Inject constructor(
        @ApplicationContext context: Context
) {

    private val defaultMessage = context.getString(R.string.common_no_internet)
    private val publisher = PublishSubject.create<String>()

    fun observe(): Observable<String> = publisher

    fun next(){
        publisher.onNext(defaultMessage)
    }

}