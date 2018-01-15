package dev.olog.presentation.activity_preferences.neural_network

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import dev.olog.domain.entity.Album
import dev.olog.domain.interactor.tab.GetAllAlbumsUseCase
import io.reactivex.Single
import javax.inject.Inject

class NeuralNetworkPresenter @Inject constructor(
        private val contentResolver: ContentResolver,
        getAllAlbumsUseCase: GetAllAlbumsUseCase
) {


    val getImagesAlbum: Single<List<Album>> = getAllAlbumsUseCase.execute()
            .firstOrError()
            .map {
                val result = mutableListOf<Album>()
                for (album in it) {
                    try {
                        MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(album.image)) // todo can return an already neural image
                        result.add(album)
                        break
                    } catch (ex: Exception){/*no image */}
                }
                result
            }

}