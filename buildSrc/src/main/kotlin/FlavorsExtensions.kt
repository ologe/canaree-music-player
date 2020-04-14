class FlavorOptions(
    val entryModule: String
)

val featureFlavors: Map<String, FlavorOptions>
    get() {
        return mapOf(
            "full" to FlavorOptions(":flavors:flavor-full"),
            "lite" to FlavorOptions(":flavors:flavor-lite")
        )
    }