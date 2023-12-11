package dev.olog.presentation.thanks

import android.content.Context
import dev.olog.presentation.R

class SpecialThanksPresenter (context: Context) {

    val data = listOf(
        SpecialThanksItem(
            context.getString(R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            R.drawable.vd_folder
        ), // folder icon

        SpecialThanksItem(
            context.getString(R.string.about_icon_made_by_x_from_y, "Smashicons", "www.flaticon.com "),
            R.drawable.vd_playlist
        ), // playlist icon

        SpecialThanksItem(
            context.getString(R.string.about_icon_made_by_x_from_y, "Popcic", "www.flaticon.com "),
            R.drawable.vd_musical_note
        ), // song icon

        SpecialThanksItem(
            context.getString(R.string.about_icon_made_by_x_from_y, "Those Icons", "www.flaticon.com "),
            R.drawable.vd_album
        ), // album icon

        SpecialThanksItem(
            context.getString(R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            R.drawable.vd_artist
        ), // artist icon

        SpecialThanksItem(
            context.getString(R.string.about_icon_made_by_x_from_y, "Nikita Golubev", "www.flaticon.com "),
            R.drawable.vd_genre
        ), // genre icon
        SpecialThanksItem(
            "Radio by Jardson Almeida from the Noun Project",
            R.drawable.vd_podcast
        ), // genre icon
        SpecialThanksItem(
            context.getString(R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            R.drawable.vd_video
        ), // video
        SpecialThanksItem(
            context.getString(R.string.about_icon_made_by_x_from_y, "Icomoon", "www.flaticon.com "),
            R.drawable.vd_lyrics
        )
    )

}