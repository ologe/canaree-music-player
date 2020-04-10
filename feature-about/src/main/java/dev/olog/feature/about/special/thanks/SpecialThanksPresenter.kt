package dev.olog.feature.about.special.thanks

import android.content.Context
import dev.olog.feature.about.R
import dev.olog.feature.about.model.SpecialThanksModel
import dev.olog.feature.presentation.base.model.PresentationId.Companion.headerId

internal class SpecialThanksPresenter(
    context: Context
) {

    val data = listOf(
        SpecialThanksModel(
            R.layout.item_special_thanks,
            headerId("folder img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            R.drawable.vd_folder
        ), // folder icon

        SpecialThanksModel(
            R.layout.item_special_thanks,
            headerId("playlist img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Smashicons", "www.flaticon.com "),
            R.drawable.vd_playlist
        ), // playlist icon

        SpecialThanksModel(
            R.layout.item_special_thanks,
            headerId("music not img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Popcic", "www.flaticon.com "),
            R.drawable.vd_musical_note
        ), // song icon

        SpecialThanksModel(
            R.layout.item_special_thanks,
            headerId("album img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Those Icons", "www.flaticon.com "),
            R.drawable.vd_album
        ), // album icon

        SpecialThanksModel(
            R.layout.item_special_thanks,
            headerId("artist img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            R.drawable.vd_artist
        ), // artist icon

        SpecialThanksModel(
            R.layout.item_special_thanks,
            headerId("genre img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Nikita Golubev", "www.flaticon.com "),
            R.drawable.vd_genre
        ), // genre icon
        SpecialThanksModel(
            R.layout.item_special_thanks,
            headerId("podcast img id"),
            "Radio by Jardson Almeida from the Noun Project",
            R.drawable.vd_podcast
        ), // genre icon
        SpecialThanksModel(
            R.layout.item_special_thanks,
            headerId("video img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            R.drawable.vd_video
        ), // video
        SpecialThanksModel(
            R.layout.item_special_thanks,
            headerId("lyrics img id"),
            context.getString(R.string.about_icon_made_by_x_from_y, "Icomoon", "www.flaticon.com "),
            R.drawable.vd_lyrics
        )
    )

}