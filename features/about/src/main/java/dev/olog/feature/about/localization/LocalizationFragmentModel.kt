package dev.olog.feature.about.localization

import dev.olog.feature.about.R

internal sealed class LocalizationFragmentModel {

    abstract val layoutType: Int

    object Help : LocalizationFragmentModel() {
        override val layoutType: Int = R.layout.item_translations_help
    }
    object Header : LocalizationFragmentModel() {
        override val layoutType: Int = R.layout.item_translations_header
    }

    data class Contributor(
        val name: String
    ) : LocalizationFragmentModel() {

        override val layoutType: Int = R.layout.item_translations_contributor
    }

}