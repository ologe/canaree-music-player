package dev.olog.feature.about.localization

import androidx.annotation.LayoutRes
import dev.olog.feature.about.R

internal sealed class LocalizationFragmentModel(
    @LayoutRes open val layoutType: Int
) {

    object Help : LocalizationFragmentModel(R.layout.item_translations_help)
    object Header : LocalizationFragmentModel(R.layout.item_translations_header)

    data class Contributor(
        val name: String
    ) : LocalizationFragmentModel(R.layout.item_translations_contributor)

}