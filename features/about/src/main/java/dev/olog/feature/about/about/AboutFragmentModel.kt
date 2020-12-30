package dev.olog.feature.about.about

internal enum class AboutFragmentType {
    AUTHOR,
    THIRD_SOFTWARE,
    COMMUNITY,
    BETA,
    SPECIAL_THANKS,
    LOCALIZATION,
    PRIVACY_POLICY,
    CHANGELOG,
    GITHUB,
}

internal data class AboutFragmentModel(
    val type: AboutFragmentType,
    val title: String,
    val subtitle: String,
)