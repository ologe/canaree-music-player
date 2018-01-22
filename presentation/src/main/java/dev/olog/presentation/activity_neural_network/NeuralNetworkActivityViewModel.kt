package dev.olog.presentation.activity_neural_network

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.ContentResolver
import android.net.Uri
import dev.olog.domain.entity.Album
import dev.olog.domain.interactor.GetAllAlbumsForUtilsUseCase
import dev.olog.shared_android.neural.NeuralImages
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class NeuralNetworkActivityViewModel(
        private val contentResolver: ContentResolver,
        getAllAlbumsForUtilsUseCase: GetAllAlbumsForUtilsUseCase

) : ViewModel() {

    private val stylePublisher = BehaviorSubject.createDefault(false)
    private val imagePublisher = BehaviorSubject.createDefault(false)

    val currentNeuralStyle = MutableLiveData<Int>()
    val currentNeuralImage = MutableLiveData<String>()

    val getImagesAlbum: Single<List<Album>> = getAllAlbumsForUtilsUseCase.execute()
            .observeOn(Schedulers.computation())
            .firstOrError()
            .map {
                val result = mutableListOf<Album>()
                for (album in it) {
                    try {
                        contentResolver.openInputStream(Uri.parse(album.image))
                        result.add(album)
                    } catch (ex: Exception){/*no image */}
                }
                result
            }

    fun updateCurrentNeuralStyle(stylePosition: Int){
        NeuralImages.setStyle(stylePosition)
        currentNeuralStyle.value = stylePosition
        stylePublisher.onNext(true)
    }

    fun updateCurrentNeuralImage(image: String){
        currentNeuralImage.value = image
        stylePublisher.onNext(true)
    }

    val observeImageLoadedSuccesfully = Observables.combineLatest(
            stylePublisher, imagePublisher, { a, b -> a && b })
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())

}