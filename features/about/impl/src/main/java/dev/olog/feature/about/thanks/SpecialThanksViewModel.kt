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
        SpecialThanksItem(
            title = context.getString(localization.R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            image = dev.olog.ui.R.drawable.vd_folder
        ), // folder icon

        SpecialThanksItem(
            title = context.getString(localization.R.string.about_icon_made_by_x_from_y, "Smashicons", "www.flaticon.com "),
            image = dev.olog.ui.R.drawable.vd_playlist
        ), // playlist icon

        SpecialThanksItem(
            title = context.getString(localization.R.string.about_icon_made_by_x_from_y, "Popcic", "www.flaticon.com "),
            image = dev.olog.ui.R.drawable.vd_musical_note
        ), // song icon

        SpecialThanksItem(
            title = context.getString(localization.R.string.about_icon_made_by_x_from_y, "Those Icons", "www.flaticon.com "),
            image = dev.olog.ui.R.drawable.vd_album
        ), // album icon

        SpecialThanksItem(
            title = context.getString(localization.R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            image = dev.olog.ui.R.drawable.vd_artist
        ), // artist icon

        SpecialThanksItem(
            title = context.getString(localization.R.string.about_icon_made_by_x_from_y, "Nikita Golubev", "www.flaticon.com "),
            image = dev.olog.ui.R.drawable.vd_genre
        ), // genre icon
        SpecialThanksItem(
            title = "Radio by Jardson Almeida from the Noun Project",
            image = dev.olog.ui.R.drawable.vd_podcast
        ), // genre icon
        SpecialThanksItem(
            title = context.getString(localization.R.string.about_icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
            image = dev.olog.ui.R.drawable.vd_video
        ), // video
        SpecialThanksItem(
            title = context.getString(localization.R.string.about_icon_made_by_x_from_y, "Icomoon", "www.flaticon.com "),
            image = dev.olog.ui.R.drawable.vd_lyrics
        )
    )

}