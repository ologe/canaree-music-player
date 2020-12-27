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

main()

fun main() {
    if (!sanityChecks()) {
        return
    }

    val fromModule = args[0]
    val toModule = args[1]
    val resourceType = args[2]
    var fileName = args[3]
    if (File(fileName).extension.isBlank()) {
        // append .xml if no extensions is explicited
        fileName = fileName + ".xml"
    }

    val rootDir = Paths.get("").toAbsolutePath().parent
    val fromPath = "$rootDir/${fromModule}/src/main/res/${resourceType}"

    val file = File(fromPath, fileName)

    if (!file.exists()) {
        println("File='${file.path}' NOT FOUND")
        println("EXITING ...")
        return
    }

    val parentFile = file.parentFile.parentFile

    // find all files in all values-X folders
    val filesToMove = parentFile.listFiles()!!
        .filter { it.name.startsWith(resourceType) }
        .map { File(it, fileName) }
        .filter { it.exists() }
        .map { it.absolutePath } // convert to path so list is not changed

    println("found ${filesToMove.size} files")

    // change the update
    filesToMove.forEachIndexed { index, f ->
        val oldFolder = "${rootDir}/${fromModule}/"
        val newModulePath = "${rootDir}/${toModule}/${f.drop(oldFolder.length)}"

        val p = File(newModulePath).parentFile
        if (!p.exists()) {
            p.mkdirs()
        }

        Files.move(File(f).toPath(), File(newModulePath).toPath(), StandardCopyOption.REPLACE_EXISTING)

        println("moved ${index + 1}/${filesToMove.size}")
    }

    println("Done")


}

enum class ResourceType(val value: String) {
    ANIM("anim"),
    ANIMATOR("animator"),
    COLOR("color"),
    DRAWABLE("drawable"),
    LAYOUT("layout"),
    MENU("menu"),
    VALUES("values"),
    XML("xml")
}

fun sanityChecks(): Boolean {
    if (args.size != 4) {
        println("""
            4 arguments needed:
              1) from module
              2) to module
              3) resource type
              4) file name
        """.trimIndent())
        return false
    }

    val fromModule = args[0]
    val toModule = args[1]
    val resourceType = args[2]
    val fileName = args[3]

    if (fromModule.isBlank()) {
        println("from module can not be empty")
        return false
    }

    if (toModule.isBlank()) {
        println("to module can not be empty")
        return false
    }
    if (resourceType.isBlank()) {
        println("from module can not be empty")
        return false
    }

    if (fileName.isBlank()) {
        println("file name can not be empty")
        return false
    }

    if (!File("../${fromModule}").exists()) {
        println(":${fromModule} not found")
        return false
    }
    if (!File("../${toModule}").exists()) {
        println(":${toModule} not found")
        return false
    }
    if (fromModule == toModule) {
        println("can't move to the same module")
        return false
    }

    if (resourceType !in ResourceType.values().map { it.value }) {
        println("resource type=${resourceType} not valid")
        println("should be one of [${ResourceType.values().joinToString { it.value }}]")
        return false
    }

    return true
}






