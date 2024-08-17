package dev.olog.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import dev.olog.core.gateway.base.BaseGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.DataObserver
import dev.olog.shared.android.Permissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

internal abstract class BaseRepository<T, Param>(
    private val context: Context,
    protected val contentResolver: ContentResolver,
    private val schedulers: Schedulers
) : BaseGateway<T, Param> {

    private val scope = MainScope()
    private val cached = channelFlow {
        Permissions.awaitStorage(context)

        send(withContext(Dispatchers.IO) { queryAll() })

        val contentUri = registerMainContentUri()

        contentResolver.registerContentObserver(
            contentUri.uri,
            contentUri.notifyForDescendants,
            DataObserver(scope) {
                send(withContext(Dispatchers.IO) { queryAll() })
            }
        )

        awaitCancellation()
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    protected val channel: StateFlow<List<T>?>
        get() = cached

    override fun getAll(): List<T> {
        return channel.value ?: queryAll() // fallback to normal query if channel never emitted
    }

    override fun observeAll(): Flow<List<T>> {
        return channel.filterNotNull()
    }

    protected fun <R> observeByParamInternal(
        contentUri: ContentUri,
        action: () -> R
    ): Flow<R> {

        return channelFlow {
            send(withContext(Dispatchers.IO) { action() })

            val observer = DataObserver(scope) {
                send(withContext(Dispatchers.IO) { action() })
            }

            contentResolver.registerContentObserver(
                contentUri.uri,
                contentUri.notifyForDescendants,
                observer
            )
            awaitClose {
                contentResolver.unregisterContentObserver(observer)
            }
        }
    }

    protected abstract fun registerMainContentUri(): ContentUri
    protected abstract fun queryAll(): List<T>

}

class ContentUri(
    val uri: Uri,
    val notifyForDescendants: Boolean
)