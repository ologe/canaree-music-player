package dev.olog.presentation.activity_preferences.neural_network

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import dev.olog.domain.entity.Album
import dev.olog.domain.interactor.GetAllAlbumsForUtilsUseCase
import dev.olog.shared_android.neural.NeuralImages
import io.reactivex.Single

class NeuralNetworkFragmentViewModel (
        private val contentResolver: ContentResolver,
        getAllAlbumsForUtilsUseCase: GetAllAlbumsForUtilsUseCase

) : ViewModel() {

    val currentNeuralStyle = MutableLiveData<Int>()

    val getImagesAlbum: Single<List<Album>> = getAllAlbumsForUtilsUseCase.execute()
            .firstOrError()
            .map {
                val result = mutableListOf<Album>()
                for (album in it) {
                    try {
                        MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(album.image))
                        result.add(album)
                        break
                    } catch (ex: Exception){/*no image */}
                }
                result
            }

    fun updateCurrentNeuralStyle(stylePosition: Int){
        NeuralImages.setStyle(stylePosition)
        currentNeuralStyle.value = stylePosition
    }

}