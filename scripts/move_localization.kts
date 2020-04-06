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
    val fromModule = args[0]
    val toModule = args[1]
    val fileName = args[2]

    if (!sanityChecks(fromModule, toModule)) {
        return
    }

    val rootDir = Paths.get("").toAbsolutePath().parent
    val fromPath = "$rootDir/${fromModule}/src/main/res/values"

    val file = File(fromPath, fileName)

    if (!file.exists()) {
        println("File='${file.path}' NOT FOUND")
        println("EXITING ...")
        return
    }

    val parentFile = file.parentFile.parentFile

    // find all files in all values-X folders
    val filesToMove = parentFile.listFiles()!!
            .filter { it.name.startsWith("values") }
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

        println("done ${index}/${filesToMove.size}")
    }

    println("Done")


}

fun sanityChecks(fromModule: String, toModule: String): Boolean {
    if (args.size != 3) {
        println("the arguments needed")
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
    return true
}







