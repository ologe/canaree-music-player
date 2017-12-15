package dev.olog.presentation.fragment_special_thanks

import android.content.Context
import dev.olog.presentation.R
import dev.olog.shared.ApplicationContext
import javax.inject.Inject

class SpecialThanksPresenter @Inject constructor(
        @ApplicationContext context: Context
) {

    val data = listOf(
            SpecialThanksModel(R.layout.item_special_thanks, "folder img id",
                    context.getString(R.string.icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
                    R.drawable.vd_folder), // folder icon

            SpecialThanksModel(R.layout.item_special_thanks, "playlist img id",
                    context.getString(R.string.icon_made_by_x_from_y, "Smashicons", "www.flaticon.com "),
                    R.drawable.vd_playlist), // playlist icon

            SpecialThanksModel(R.layout.item_special_thanks, "music not img id",
                    context.getString(R.string.icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
                    R.drawable.vd_musical_note), // album icon

            SpecialThanksModel(R.layout.item_special_thanks, "album img id",
                    context.getString(R.string.icon_made_by_x_from_y, "Those Icons", "www.flaticon.com "),
                    R.drawable.vd_album), // album icon

            SpecialThanksModel(R.layout.item_special_thanks, "artist img id",
                    context.getString(R.string.icon_made_by_x_from_y, "Freepik", "www.flaticon.com "),
                    R.drawable.vd_artist), // artist icon

            SpecialThanksModel(R.layout.item_special_thanks, "genre img id",
                    context.getString(R.string.icon_made_by_x_from_y, "Nikita Golubev", "www.flaticon.com "),
                    R.drawable.vd_genre) // genre icon
    )

}