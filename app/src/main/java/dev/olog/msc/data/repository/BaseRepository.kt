//package dev.olog.msc.data.repository
//
//import dev.olog.msc.domain.gateway.BaseGateway
//import io.reactivex.Observable
//import java.util.concurrent.TimeUnit
//
//abstract class BaseRepository<T, Param> : BaseGateway<T, Param> {
//
//    private val cachedDataStore = CachedDataStore<T>()
//
//    protected abstract fun queryAllData(): Observable<List<T>>
//
//    private fun queryAndCache() : Observable<List<T>> {
//        return queryAllData().doOnNext { cachedDataStore.updateCache(it) }
//    }
//
//    override fun getAll(): Observable<List<T>> {
//        if (cachedDataStore.isEmpty()){
//            return Observable.concat(
//                    queryAndCache().take(1),
//                    queryAndCache().skip(1).debounce(1, TimeUnit.SECONDS)
//            )
//        }
//
//        return Observable.concat(
//                cachedDataStore.getAll(),
//                queryAndCache().skip(1).debounce(1, TimeUnit.SECONDS)
//        )
//    }
//
//    override fun getByParam(param: Param): Observable<T> {
//        return getAll().map { getByParamImpl(it, param) }
//    }
//
//    protected abstract fun getByParamImpl(list: List<T>, param: Param) : T
//
//
//}