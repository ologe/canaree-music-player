package dev.olog.feature.about.special.thanks

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.feature.about.R

internal class SpecialThanksFragmentViewModel @ViewModelInject constructor(
    @ApplicationContext context: Context
): ViewModel() {

    val data = listOf(
        SpecialThanksFragmentModel(
            title = context.getString(R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            drawable = R.drawable.vd_folder
        ), // folder icon

        SpecialThanksFragmentModel(
            title = context.getString(R.string.about_icon_made_by_x_from_y, "Smashicons", "www.flaticon.com "),
            drawable = R.drawable.vd_playlist
        ), // playlist icon

        SpecialThanksFragmentModel(
            title = context.getString(R.string.about_icon_made_by_x_from_y, "Popcic", "www.flaticon.com "),
            drawable = R.drawable.vd_musical_note
        ), // song icon

        SpecialThanksFragmentModel(
            title = context.getString(R.string.about_icon_made_by_x_from_y, "Those Icons", "www.flaticon.com "),
            drawable = R.drawable.vd_album
        ), // album icon

        SpecialThanksFragmentModel(
            title = context.getString(R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            drawable = R.drawable.vd_artist
        ), // artist icon

        SpecialThanksFragmentModel(
            title = context.getString(R.string.about_icon_made_by_x_from_y, "Nikita Golubev", "www.flaticon.com "),
            drawable = R.drawable.vd_genre
        ), // genre icon
        SpecialThanksFragmentModel(
            title = "Radio by Jardson Almeida from the Noun Project",
            drawable = R.drawable.vd_podcast
        ), // genre icon
        SpecialThanksFragmentModel(
            title = context.getString(R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            drawable = R.drawable.vd_video
        ), // video
        SpecialThanksFragmentModel(
            title = context.getString(R.string.about_icon_made_by_x_from_y, "Icomoon", "www.flaticon.com "),
            drawable = R.drawable.vd_lyrics
        )
    )

}