package dev.olog.feature.about.thanks

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.feature.about.R
import javax.inject.Inject

@HiltViewModel
class SpecialThanksViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    val data = listOf(
        SpecialThanksModel(
            R.layout.item_special_thanks,
            MediaId.headerId("folder img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            R.drawable.vd_folder
        ), // folder icon

        SpecialThanksModel(
            R.layout.item_special_thanks,
            MediaId.headerId("playlist img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Smashicons", "www.flaticon.com "),
            R.drawable.vd_playlist
        ), // playlist icon

        SpecialThanksModel(
            R.layout.item_special_thanks,
            MediaId.headerId("music not img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Popcic", "www.flaticon.com "),
            R.drawable.vd_musical_note
        ), // song icon

        SpecialThanksModel(
            R.layout.item_special_thanks,
            MediaId.headerId("album img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Those Icons", "www.flaticon.com "),
            R.drawable.vd_album
        ), // album icon

        SpecialThanksModel(
            R.layout.item_special_thanks,
            MediaId.headerId("artist img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            R.drawable.vd_artist
        ), // artist icon

        SpecialThanksModel(
            R.layout.item_special_thanks,
            MediaId.headerId("genre img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Nikita Golubev", "www.flaticon.com "),
            R.drawable.vd_genre
        ), // genre icon
        SpecialThanksModel(
            R.layout.item_special_thanks,
            MediaId.headerId("podcast img id"),
            "Radio by Jardson Almeida from the Noun Project",
            R.drawable.vd_podcast
        ), // genre icon
        SpecialThanksModel(
            R.layout.item_special_thanks,
            MediaId.headerId("video img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            R.drawable.vd_video
        ), // video
        SpecialThanksModel(
            R.layout.item_special_thanks,
            MediaId.headerId("lyrics img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Icomoon", "www.flaticon.com "),
            R.drawable.vd_lyrics
        )
    )

}