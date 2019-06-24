package dev.olog.msc.domain.interactor.update

import android.content.Context
import com.crashlytics.android.Crashlytics
import dev.olog.injection.IoSchedulers
import dev.olog.msc.catchNothing
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.gateway.UsedImageGateway
import dev.olog.core.interactor.CompletableUseCaseWithParam
import dev.olog.msc.notifyItemChanged
import io.reactivex.Completable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class UpdateTrackUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    schedulers: IoSchedulers,
    private val gateway: UsedImageGateway

): CompletableUseCaseWithParam<UpdateTrackUseCase.Data>(schedulers){

    override fun buildUseCaseObservable(param: Data): Completable {
        return Completable.create {
            try {
                val file = File(param.path)
                val audioFile = AudioFileIO.read(file)
                val tag = audioFile.tagOrCreateAndSetDefault
                try {
                    tag.setEncoding("UTF-8")
                } catch (ex: Exception){
                    Crashlytics.logException(ex)
                }

                for (field in param.fields) {
                    catchNothing { tag.setField(field.key, field.value) }
                }

                audioFile.commit()

                if (param.id != null){
                    gateway.setForTrack(param.id, param.image)
                }


                notifyItemChanged(context, param.path)

                it.onComplete()
            } catch (ex: Exception){
                it.onError(ex)
            }
        }
    }

    data class Data(
            val id: Long?,
            val path: String,
            val image: String?,
            val fields: Map<FieldKey, String>
    )

}