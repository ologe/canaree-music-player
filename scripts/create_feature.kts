#!/bin/bash

//usr/bin/env echo '
/**** BOOTSTRAP kscript ****\'>/dev/null
command -v kscript >/dev/null 2>&1 || curl -L "https://git.io/fpF1K" | bash 1>&2
exec kscript $0 "$@"
\*** IMPORTANT: Any code including imports and annotations must come after this line ***/


import java.io.File
import java.nio.file.Paths

main()

object Constants {
    const val BasePackage = "dev.olog.feature"
    const val FeatureFolderName = "features"
    const val GradleFileType = "gradle" // or gradle.kts

    val subModulesToCreate: List<Module> = listOf(
        ApiModule(), ImplModule()
    )
}

@Suppress("EnumEntryName")
enum class SourceType {
    androidTest,
    main,
    test
}

fun main() {
    val moduleName = args[0]

    val rootDir = Paths.get("").toAbsolutePath().parent.toFile()

    // /features
    val featuresGroupFolder = File(rootDir, Constants.FeatureFolderName)
    featuresGroupFolder.mkdir()

    // /features/$moduleName
    val featureFolder = File(featuresGroupFolder, moduleName)
    featureFolder.mkdir()

    // /features/$moduleName/api
    Constants.subModulesToCreate.forEach {
        val root = File(featureFolder, it.subModuleName)
        root.mkdir()
        it.create(root, moduleName)
    }

    // add all created modules to settings.gradle
    val settingsGradle = File(rootDir, "settings.${Constants.GradleFileType}")
    settingsGradle.appendText("\n\n")
    settingsGradle.appendText(
        Constants.subModulesToCreate.joinToString(
            prefix = "include ",
            separator = ", "
        ) {
            "':${Constants.FeatureFolderName}:${moduleName}:${it.subModuleName}'"
        }

    )
}

interface Module {
    val subModuleName: String
    fun create(root: File, moduleName: String) {
        createBuildGradle(root, moduleName)

        val sourceFolder = File(root, "src")

        for (sourceType in SourceType.values()) {
            val sourceTypeFolder = File(sourceFolder, sourceType.toString())
            sourceTypeFolder.mkdir()
            if (sourceType == SourceType.main) {
                createManifest(sourceTypeFolder, moduleName)
            }
            val file = createJavaPackage(sourceTypeFolder, moduleName)
            createFilesForSource(sourceType, file, moduleName)
        }
    }

    private fun createManifest(
        root: File,
        moduleName: String,
    ) {
        val manifest = File(root, "AndroidManifest.xml")
        manifest.createNewFile()
        manifest.bufferedWriter().use {
            it.append(
                """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="${Constants.BasePackage}.$moduleName">
    ${buildManifestContent(moduleName)}
</manifest>
""".trimIndent()
            )
        }
    }

    private fun createBuildGradle(root: File, moduleName: String) {
        val file = File(root, "build.${Constants.GradleFileType}")
        file.createNewFile()
        file.bufferedWriter().use {
            it.append(buildGradleContent(moduleName).trimIndent())
        }
    }

    /**
     * @param root sourceTypeFolder
     */
    private fun createJavaPackage(root: File, moduleName: String): File {
        // eg: features/test/api/src/androidTest/java/dev/olog/feature/
        val path = File("${root.absolutePath}/java/${Constants.BasePackage.replace(".", "/")}/$moduleName")
        path.mkdirs()
        return path
    }

    fun buildGradleContent(moduleName: String): String

    fun buildManifestContent(moduleName: String): String = ""

    fun createFilesForSource(sourceType: SourceType, root: File, moduleName: String) {

    }
}

class ApiModule : Module {

    override val subModuleName: String = "api"

    //    @Language("Groovy")
    override fun buildGradleContent(moduleName: String) = """
plugins {
    id 'com.android.library'
    id 'kotlin-android'
    id 'kotlin-kapt'
}
apply from: rootProject.file("buildscripts/android-defaults.gradle")
dependencies {
    implementation project(':core')
    implementation project(':localization')
}
""".trimIndent()
}

class ImplModule : Module {

    override val subModuleName: String = "impl"

    //    @Language("Groovy")
    override fun buildGradleContent(moduleName: String) = """
plugins {
    id 'com.android.library'
    id 'kotlin-android'
    id 'kotlin-android-extensions'
    id 'kotlin-kapt'
    id 'dagger.hilt.android.plugin'
}
apply from: rootProject.file("buildscripts/android-defaults.gradle")
dependencies {
    // features
    implementation project(':features:base')
    api project(':${Constants.FeatureFolderName}:$moduleName:api')
    // libs
    implementation project(':core')
    implementation project(':image-provider')
    implementation project(':media')
    
    // shared
    implementation project(':localization')
    implementation project(':shared')
    implementation project(':shared-android')
    implementation project(':shared-widgets')
    implementation project(':prefs-keys')
    implementation lib_core.hilt
    kapt lib_core.hilt_processor
    
    // androidx
    implementation lib_x.appcompat
    implementation lib_x.material
    implementation lib_x.core
    implementation lib_x.constraint_layout
    implementation lib_x.fragments
    
    implementation lib_ui.scroll_helper
}
""".trimIndent()
}