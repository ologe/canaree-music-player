package dev.olog.data.spotify.extensions

import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow

internal fun WorkManager.getWorkInfoAsFlow(tag: String): Flow<List<WorkInfo>> {
    return getWorkInfosByTagLiveData(tag).asFlow()
}