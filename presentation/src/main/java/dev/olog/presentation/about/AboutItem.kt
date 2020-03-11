package dev.olog.presentation.about

data class AboutItem(
    val type: Int,
    val itemType: AboutItemType,
    val title: String,
    val subtitle: String
)

enum class AboutItemType {
    AUTHOR_ID,
    VERSION,
    THIRD_SW_ID,
    COMMUNITY,
    BETA,
    SPECIAL_THANKS_ID,
    TRANSLATION,
    RATE_ID,
    PRIVACY_POLICY,
    CHANGELOG,
    GITHUB
}