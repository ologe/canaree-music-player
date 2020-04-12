class FlavorOptions(
    val entryModule: String
)

@Suppress("UNCHECKED_CAST")
val featureFlavors: Map<String, FlavorOptions>
    get() {
        return mapOf(
            "full" to FlavorOptions(":flavors:flavor-full"),
            "lite" to FlavorOptions(":flavors:flavor-lite")
        )
    }