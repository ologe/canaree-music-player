package dev.olog.presentation.fragment_edit_info

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.net.ConnectivityManager
import dev.olog.presentation.R
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.Constants
import dev.olog.shared_android.extension.isNetworkAvailable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URLEncoder
import javax.inject.Inject

private const val GOOGLE_QUERY = "http://www.google.it/search?q="

class AutoTag @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val view: EditInfoFragmentView,
        private val editInfoFragmentPresenter: EditInfoFragmentPresenter,
        private val connectivityManager: ConnectivityManager

) : DefaultLifecycleObserver {

    private var queryInProgress = false
    private var autoTagQueryResult : AutoTagQueryResult? = null

    init {
        lifecycle.addObserver(this)
    }

    private var disposable : Disposable? = null

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

    fun getTags(){
        if (queryInProgress){
            return
        }

        if (autoTagQueryResult != null){
            showData(autoTagQueryResult!!)
            return
        }

        if (!connectivityManager.isNetworkAvailable()){
            view.showToast(R.string.network_not_available)
            return
        }
        queryInProgress = true

        disposable.unsubscribe()

        disposable = getBaseQuery()
                .flatMap { Single.zip(
                        makeObservable(it, "album"),
                        makeObservable(it, "artist"),
                        getFirstNotNull)
                }.observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view.toggleLoading(true) }
                .doOnEvent { _, _ -> view.toggleLoading(false) }
                .doAfterTerminate { queryInProgress = false }
                .subscribe({ result ->

                    autoTagQueryResult = result

                    val emptyTitle = result.title.isBlank()
                    val emptyArtist = result.artist.isBlank()
                    val emptyAlbum = result.album.isBlank()

                    if (emptyTitle && emptyArtist && emptyAlbum){
                        view.showToast(R.string.edit_info_auto_tag_no_results)
                    } else {
                       showData(result)
                    }

                }, Throwable::printStackTrace)
    }

    private fun showData(result: AutoTagQueryResult){
        val emptyTitle = result.title.isBlank()
        val emptyArtist = result.artist.isBlank()
        val emptyAlbum = result.album.isBlank()

        if (!emptyAlbum){
            view.setAlbum(result.album)
        }

        if (!emptyArtist){
            view.setArtist(result.artist)
        }

        if (!emptyTitle){
            view.setTitle(result.title)
        }
    }

    private fun getBaseQuery(): Single<String> {
        return editInfoFragmentPresenter.getSong()
                .map {
                    var result = it.title
                    if (it.artist != Constants.UNKNOWN){
                       result += it.artist
                    }
                    result
                }
                .map { it.replaceFirst("(?i)official audio".toRegex(), "")
                            .replaceFirst("(?i)official video".toRegex(), "")
                            .replaceFirst("(?i)official music".toRegex(), "")
                            .replaceFirst("(?i)official lyric".toRegex(), "")
                            .replaceFirst("(?i)official lyrics".toRegex(), "")
                            .replaceFirst("(?i)music video".toRegex(), "")
                            .replaceFirst("(?i)lyrics".toRegex(), "")
                            .replaceFirst("(?i)explicit".toRegex(), "")
                            .replaceFirst("(?i)explicit audio".toRegex(), "")
                            .replaceFirst("(?i)(audio)".toRegex(), "")
                            .replaceFirst("(?i)(video)".toRegex(), "")
                            .replaceFirst("(?i)(lyrics)".toRegex(), "")
                            .replaceFirst("(?i)(freestyle)".toRegex(), "")
                            .replaceFirst("(?i)(hd)".toRegex(), "")
                }
    }

    private fun makeObservable(baseQuery: String, artistOrAlbum: String) : Single<AutoTagQueryResult>{
        var single = Single.fromCallable { search(baseQuery + " " + artistOrAlbum) }
                .subscribeOn(Schedulers.newThread())


        val milestone = listOf(
                baseQuery.toLowerCase().indexOf("ft"),
                baseQuery.toLowerCase().indexOf("prod"),
                baseQuery.toLowerCase().indexOf("feat"))

        for (truncateStart in milestone) {
            if (truncateStart == -1) {
                continue
            }

            val truncateEnd = baseQuery.indexOf("-", truncateStart)

            val newBaseQuery = if (truncateEnd != -1){
                // substitute useless string from 'ft to -' with empty string
                baseQuery.substring(0, truncateStart) + "" + baseQuery.substring(truncateEnd, baseQuery.length)
            } else {
                baseQuery.substring(0, truncateStart)
            }

            val anotherTry = Single.fromCallable { search(newBaseQuery + " " + artistOrAlbum) }
                    .subscribeOn(Schedulers.newThread())

            single = single.zipWith(anotherTry, getFirstNotNull)
        }
        return single.subscribeOn(Schedulers.newThread())
    }

    private fun search(text: String) : AutoTagQueryResult {
//        System.out.println(GOOGLE_QUERY + URLEncoder.encode(text, "UTF-8"))
        if (text.contains("artist")) return searchForArtist(text)
        if (text.contains("album")) return searchForAlbum(text)
        throw IllegalArgumentException("not contains nor artist neither album -> $text")
    }

    /*
     *  class "_XWk" is the most common but returns only a value
     *  other classes works only on italian google
     */
    private fun searchForAlbum(text: String) : AutoTagQueryResult{
        val result = AutoTagQueryResult()

        val document = Jsoup.connect(GOOGLE_QUERY + URLEncoder.encode(text, "UTF-8")).get().body()

        result.setAlbum(document, "_XWk")
        result.setTitle(document, "kno-fb-ctx _g2g")

        if (result.album.isBlank()){
            result.setAlbum(document, "kno-ecr-pt kno-fb-ctx")

            if (result.artist.isBlank()){
                result.setArtist(document, "_Xbe kno-fv")
            }
        }

        return result
    }
//
    private fun searchForArtist(text: String) : AutoTagQueryResult {
        val result = AutoTagQueryResult()

        val document = Jsoup.connect(GOOGLE_QUERY + URLEncoder.encode(text, "UTF-8")).get().body()

        result.setArtist(document, "_XWk")
        result.setTitle(document, "kno-fb-ctx _g2g")

        if (result.artist.isBlank()){
            // work when there are more than one artists
            val anotherResultForArtists = document.getElementsByClass("title")
            try {
                anotherResultForArtists
                        .filter { notSameAsTitle(result, it) }
                        .forEach { result.addArtist(it.text()) }

            } catch (ex : IndexOutOfBoundsException){}
        }

        return result
    }

    private fun notSameAsTitle(result: AutoTagQueryResult, element: Element ): Boolean{
        return result.title != element.text()
    }

    private val getFirstNotNull : BiFunction<AutoTagQueryResult, AutoTagQueryResult, AutoTagQueryResult> =
            BiFunction { t1, t2 ->
                val title = if (t1.title.isNotBlank()){
                    t1.title
                } else {
                    if (t2.title.isNotBlank()){
                        t2.title
                    } else ""
                }

                val artist = if (t1.artist.isNotBlank()){
                    t1.artist
                } else {
                    if (t2.artist.isNotBlank()){
                        t2.artist
                    } else ""
                }

                val album = if (t1.album.isNotBlank()){
                    t1.album
                } else {
                    if (t2.album.isNotBlank()){
                        t2.album
                    } else ""
                }

                AutoTagQueryResult(title, artist, album)
            }


}

private data class AutoTagQueryResult(
        var title: String = "",
        var artist: String = "",
        var album: String = ""
) {

    fun setTitle(document: Element, documentClass: String){
        this.title = tryWithClass(document, documentClass)
    }

    fun setArtist(document: Element, documentClass: String){
        this.artist = tryWithClass(document, documentClass)
    }

    fun setAlbum(document: Element, documentClass: String){
        this.album = tryWithClass(document, documentClass)
    }

    fun addArtist(artist: String){
        if (artist.isBlank()){
            this.artist = artist
        } else {
            this.artist += ", $artist"
        }
    }

    fun tryWithClass(document: Element, documentClass: String): String{
        try {
            val tags = document.getElementsByClass(documentClass)
            return tags[0].text()
        } catch (ex: IndexOutOfBoundsException){
            return ""
        }
    }

}