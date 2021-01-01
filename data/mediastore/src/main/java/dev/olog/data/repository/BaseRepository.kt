package dev.olog.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import dev.olog.domain.gateway.base.BaseGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.data.DataObserver
import dev.olog.data.utils.PermissionsUtils
import dev.olog.shared.ConflatedSharedFlow
import dev.olog.shared.value
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal abstract class BaseRepository<T, Param>(
    private val context: Context,
    private val schedulers: Schedulers
) : BaseGateway<T, Param> {

    protected val contentResolver: ContentResolver = context.contentResolver

    protected val publisher = ConflatedSharedFlow<List<T>?>(null)

    protected fun firstQuery() {
        GlobalScope.launch(schedulers.io) {

            do {
                delay(200)
            } while (!PermissionsUtils.canReadStorage(context))

            val contentUri = registerMainContentUri()

            contentResolver.registerContentObserver(
                contentUri.uri,
                contentUri.notifyForDescendants,
                DataObserver(schedulers.io) { publisher.tryEmit(queryAll()) }
            )
            publisher.tryEmit(queryAll())
        }
    }

    override suspend fun getAll(): List<T> {
        return publisher.value ?: queryAll()
    }

    override fun observeAll(): Flow<List<T>> = publisher.filterNotNull()

    protected fun <R> observeByParamInternal(
        contentUri: ContentUri,
        action: suspend () -> R
    ): Flow<R> {

        val flow = MutableStateFlow<R?>(null)

        val observer = DataObserver(schedulers.io) {
            flow.value = action()
        }
        contentResolver.registerContentObserver(contentUri.uri, contentUri.notifyForDescendants, observer)

        return flow
            .onStart { flow.value = action() }
            .onCompletion { contentResolver.unregisterContentObserver(observer) }
            .flowOn(schedulers.io)
            .mapNotNull { it }
    }

    protected abstract fun registerMainContentUri(): ContentUri
    protected abstract suspend fun queryAll(): List<T>

}

class ContentUri(
    val uri: Uri,
    val notifyForDescendants: Boolean
)