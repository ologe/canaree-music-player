class FlavorOptions(
    val entryModule: String
)

val featureFlavors: Map<String, FlavorOptions>
    get() {
        return mapOf(
            "full" to FlavorOptions(":flavors:full"),
            "lite" to FlavorOptions(":flavors:lite")
        )
    }