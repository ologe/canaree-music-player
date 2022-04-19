package dev.olog.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import dev.olog.core.gateway.base.BaseGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.DataObserver
import dev.olog.data.utils.PermissionsUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

internal abstract class BaseRepository<T, Param>(
    private val context: Context,
    protected val contentResolver: ContentResolver,
    private val schedulers: Schedulers
) : BaseGateway<T, Param> {

    protected val channel = ConflatedBroadcastChannel<List<T>>()

    protected fun firstQuery() {
        GlobalScope.launch(schedulers.io) {

            do {
                delay(200)
            } while (!PermissionsUtils.canReadStorage(context))

            val contentUri = registerMainContentUri()

            contentResolver.registerContentObserver(
                contentUri.uri,
                contentUri.notifyForDescendants,
                DataObserver(schedulers.io) { channel.trySend(queryAll()) }
            )
            channel.trySend(queryAll())
        }
    }

    override fun getAll(): List<T> {
//        assertBackgroundThread()
        return channel.valueOrNull
            ?: queryAll() // fallback to normal query if channel never emitted
    }

    override fun observeAll(): Flow<List<T>> {
        return channel.asFlow()
    }

    protected fun <R> observeByParamInternal(
        contentUri: ContentUri,
        action: () -> R
    ): Flow<R> {

        val flow: Flow<R> = channelFlow {

            if (!isClosedForSend) {
                trySend(action())
            }

            val observer = DataObserver(schedulers.io) {
                if (!isClosedForSend) {
                    trySend(action())
                }
            }

            contentResolver.registerContentObserver(
                contentUri.uri,
                contentUri.notifyForDescendants,
                observer
            )
            awaitClose { contentResolver.unregisterContentObserver(observer) }
        }
        return flow
    }

    protected abstract fun registerMainContentUri(): ContentUri
    protected abstract fun queryAll(): List<T>

}

class ContentUri(
    val uri: Uri,
    val notifyForDescendants: Boolean
)