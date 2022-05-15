package dev.olog.feature.about

data class AboutItem(
    val title: String,
    val subtitle: String,
    val type: Type,
) {

    enum class Type {
        Havoc,
        Author,
        Version,
        Community,
        Beta,
        Rate,
        SpecialThanks,
        Translation,
        Changelog,
        Repo,
        Licence,
        PrivacyPolicy,
    }

}