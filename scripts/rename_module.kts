#!/bin/bash

//usr/bin/env echo '
/**** BOOTSTRAP kscript ****\'>/dev/null
command -v kscript >/dev/null 2>&1 || curl -L "https://git.io/fpF1K" | bash 1>&2
exec kscript $0 "$@"
\*** IMPORTANT: Any code including imports and annotations must come after this line ***/

import java.io.File
import java.nio.file.Paths

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

    // get all build.gradle.kts from modules that are not $moduleName
    val modules = rootDir
            .listFiles()!!
            .filter { it.name != moduleName } // exclude chosen module
            .map { File(it, "build.gradle.kts") }
            .filter { it.exists() }
            .filter { it.readText().contains("(\":${moduleName}\")") } // filter only files that contains the module as dependency
            .toMutableList()

    modules.add(File(rootDir, "settings.gradle.kts")) // add also settings.gradle

    println("need to update ${modules.size} gradle files")

    // move all fiels
//    Files.move(
//            currentModule.toPath(),
//            File(rootDir.absolutePath, renameTo).toPath(),
//            StandardCopyOption.REPLACE_EXISTING
//    )


    modules.take(1).forEach {

        // replaced (":moduleName") with (":renameTo")
        val newFilesContent = it.readText().replace("(\":${moduleName}\")", "(\":${renameTo}\")")
        it.writeText(newFilesContent)
        
    }

    println("done")

    // TODO update modules.xml
    // TODO update build.gradle.kts in all modules
    // TODO delete settings.gradle
    // TODO update settings.gradle.kts
}

fun ensureNewModuleNameIsWellFormatted(renameTo: String): Boolean {
    return renameTo.isNotBlank() && // not blank
            !renameTo[0].isDigit() && // don't start with digit
            regex.matches(renameTo) // contains letters, numbers and dot, underscore and dash symbol
}