#!/bin/bash

//usr/bin/env echo '
/**** BOOTSTRAP kscript ****\'>/dev/null
command -v kscript >/dev/null 2>&1 || curl -L "https://git.io/fpF1K" | bash 1>&2
exec kscript $0 "$@"
\*** IMPORTANT: Any code including imports and annotations must come after this line ***/

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

val regex = "[\\w\\.\\-\\_]+".toRegex()

main()

fun main() {
    if (args.size != 2) {
        println("""
            2 arguments needed
              1) current module name
              2) new module name
        """.trimIndent())
        return
    }
    val moduleName = args[0].trim()
    val renameTo = args[1].trim()
    if (!ensureNewModuleNameIsWellFormatted(renameTo)) {
        println("new module name not valid=${renameTo}")
        return
    }

    val rootDir = Paths.get("").toAbsolutePath().parent.toFile()
    val currentModule = File(rootDir.absolutePath, moduleName)

    if (!currentModule.exists()) {
        println("module :$moduleName not found")
        return
    }

    // get all build.gradle from modules that are not $moduleName
    val modules = searchForGradleFiles(rootDir, moduleName)

    // delete iml file
    val imlFile = File(currentModule, "$moduleName.iml")
    if (imlFile.exists()) {
        Files.delete(imlFile.toPath())
    }

    // move all fiels
    Files.move(
        currentModule.toPath(),
        File(rootDir.absolutePath, renameTo).toPath(),
        StandardCopyOption.REPLACE_EXISTING
    )

    updateGradleFiles(modules, moduleName, renameTo)
    updateModulesXmlFile(rootDir, moduleName)

    Runtime.getRuntime().exec(".././gradlew sync clean")

    println("done")
}

fun searchForGradleFiles(rootDir: File, moduleName: String): List<File> {
    println("search for gradle files")

    val modules = rootDir
        .listFiles()!!
        .filter { it.name != moduleName } // exclude chosen module
        .map { File(it, "build.gradle") }
        .filter { it.exists() }
        .filter { it.readText().contains("(\":${moduleName}\")") } // filter only files that contains the module as dependency
        .toMutableList()

    modules.add(File(rootDir, "settings.gradle")) // add also settings.gradle

    println("need to update ${modules.size} gradle files")

    return modules
}

fun updateGradleFiles(modules: List<File>, moduleName: String, renameTo: String) {
    modules.forEachIndexed { index, f ->

        // replaced (":moduleName") with (":renameTo")
        val newFilesContent = f.readText().replace("(\":${moduleName}\")", "(\":${renameTo}\")")
        f.writeText(newFilesContent)

        println("gradle rename ${index + 1}/${modules.size}")
    }
}

fun updateModulesXmlFile(rootDir: File, moduleName: String) {
    println("remove from modules.xml in .idea")
    val ideaFolder = File(rootDir, ".idea")
    val modulesFile = File(ideaFolder, "modules.xml")
    val newContent = modulesFile.readText().replaceFirst(
        "<module fileurl=\"file://\$PROJECT_DIR\$/${moduleName}/${moduleName}.iml\" filepath=\"\$PROJECT_DIR\$/${moduleName}/${moduleName}.iml\" group=\"canaree-music-player/${moduleName}\" />",
        ""
    )
    modulesFile.writeText(newContent)
    println("modules.xml updated")
}

fun ensureNewModuleNameIsWellFormatted(renameTo: String): Boolean {
    return renameTo.split(File.separator).all { m ->
        m.isNotBlank() && // not blank
            !m[0].isDigit() && // don't start with digit
            regex.matches(m) // contains letters, numbers and dot, underscore and dash symbol
    }
}