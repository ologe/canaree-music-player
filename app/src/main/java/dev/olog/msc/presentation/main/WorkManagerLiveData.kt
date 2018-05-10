package dev.olog.msc.presentation.main

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import androidx.work.State
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.utils.k.extension.toast
import dev.olog.msc.workManager
import javax.inject.Inject

class WorkManagerLiveData @Inject constructor(
        @ApplicationContext context: Context,
        lifecycleOwner: LifecycleOwner

): DefaultLifecycleObserver {

    companion object {
        const val UPDATE_TRACK = "UPDATE_TRACK"
        const val UPDATE_ALBUM = "UPDATE_ALBUM"
        const val UPDATE_ARTIST = "UPDATE_ARTIST"
    }

    init {
        workManager.getStatusesByTag(UPDATE_TRACK)
                .observe(lifecycleOwner, Observer { workStatuses ->
                    workStatuses?.forEach { status ->
                        when (status.state){
                            State.SUCCEEDED -> context.toast("success")
                            State.FAILED -> context.toast("failed")
                        }
                    }
                })

        workManager.getStatusesByTag(UPDATE_ALBUM)
                .observe(lifecycleOwner, Observer {

                })

        workManager.getStatusesByTag(UPDATE_ARTIST)
                .observe(lifecycleOwner, Observer {

                })

    }


}