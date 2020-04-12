class FlavorOptions(
    val entryModule: String
)

@Suppress("UNCHECKED_CAST")
val featureFlavors: Map<String, FlavorOptions>
    get() {
        return mapOf(
            "full" to FlavorOptions(":flavor-full"),
            "lite" to FlavorOptions(":flavor-lite")
        )
    }