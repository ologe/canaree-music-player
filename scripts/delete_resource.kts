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

    val module = args[0]
    val resourceType = args[1]
    var fileName = args[2]
    if (File(fileName).extension.isBlank()) {
        // append .xml if no extensions is explicited
        fileName = fileName + ".xml"
    }

    val rootDir = Paths.get("").toAbsolutePath().parent
    val fromPath = "$rootDir/${module}/src/main/res/${resourceType}"

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
        Files.delete(File(f).toPath())

        println("deleted ${index + 1}/${filesToMove.size}")
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
    if (args.size != 3) {
        println("""
            3 arguments needed:
              1) module
              2) resource type
              3) file name
        """.trimIndent())
        return false
    }

    val module = args[0]
    val resourceType = args[1]
    val fileName = args[2]

    if (module.isBlank()) {
        println("module can not be empty")
        return false
    }

    if (fileName.isBlank()) {
        println("file name can not be empty")
        return false
    }

    if (!File("../${module}").exists()) {
        println(":${module} not found")
        return false
    }

    if (resourceType !in ResourceType.values().map { it.value }) {
        println("resource type=${resourceType} not valid")
        println("should be one of [${ResourceType.values().joinToString { it.value }}]")
        return false
    }

    return true
}






