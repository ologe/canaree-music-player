import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.plugins.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import kotlin.system.measureTimeMillis

private const val BUILD_GRADLE = "build.gradle.kts"
private const val DAGGER_FILE_SUFFIX = "Dagger.kt"
private const val DAGGER_CLASS_SUFFIX = "Dagger"
private const val APP_MODULE = "AppModule"
private const val SRC_DIR = "src/main/java"
private const val FEATURE_MODULE_FILE = "FeaturesModule.kt"

/**
 * 1) Look for api dependencies
 * 2) For each dependent sub-project, search for files with [DAGGER_FILE_SUFFIX],
 *      and builds package-name.class-name.AppModule
 * 3) Recreates `FeaturesModule.kt` with the updated modules dependencies
 */
class DynamicFlavorPlugin : Plugin<Project> {

    //    matches 'api(project(":my-project)''
    private val regex = "api\\(project\\(\":([\\w-]+)\"\\)\\)".toRegex()

    // needed to parse kotlin files
    private val psiManager: PsiManager by lazy {
        val project = KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(),
            CompilerConfiguration(),
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        ).project
        PsiManager.getInstance(project)
    }

    override fun apply(project: Project) {
        project.plugins.all {
            when (this) {
                is AppPlugin,
                is LibraryPlugin -> {
                    project.afterEvaluate {

                    }
                }
            }
            if (this is AppPlugin) {
                project.afterEvaluate {
                    println("applied in ${measureTimeMillis { applyOnApp(this) }}")
                }
            }
        }
    }

    private fun applyOnApp(project: Project) {
        val gradleFile = File(project.projectDir, BUILD_GRADLE)

        val featureDependencies = regex.findAll(gradleFile.readText())
            .map { it.groups[1]!!.value }
            .toList()

        val dependentProjects = project.rootProject.allprojects
            .filter { it.name in featureDependencies }

        val modules = mutableListOf<String>()

        for (dependentProject in dependentProjects) {
            val projectModules = findProjectAppModules(dependentProject)
            println("found ${dependentProject.name} -> $projectModules")
            modules.addAll(projectModules)
        }

        println("\n*****\n")

        val sources = File(project.projectDir, SRC_DIR)

        val moduleFile = sources.walk().find { it.name == FEATURE_MODULE_FILE }
        if (moduleFile == null) {
            println("$FEATURE_MODULE_FILE not found")
            return
        }

        overrideFeatureModulesFile(moduleFile, modules)
    }

    private fun findProjectAppModules(project: Project): List<String> {
        val result = mutableListOf<String>()
        val sources = File(project.projectDir, SRC_DIR)
        sources.walk()
            .filter { it.name.endsWith(DAGGER_FILE_SUFFIX) }
            .forEach { file ->

                val ktFile = file.toKtFile()

                val daggerClass = ktFile.declarations
                    .filterIsInstance<KtClass>()
                    .first { it.name!!.endsWith(DAGGER_CLASS_SUFFIX) }

                result.add("${ktFile.packageFqName}.${daggerClass.name}.$APP_MODULE")
            }
        return result
    }

    private fun overrideFeatureModulesFile(moduleFile: File, modules: List<String>) {
        val ktFile = moduleFile.toKtFile()
        val className = ktFile.declarations
            .filterIsInstance<KtClass>()
            .map { it.name }
            .first()

        val text = """
package ${ktFile.packageFqName}

import dagger.Module

@Module(
    includes = [
        ${modules.joinToString(separator = ",\n\t\t") { "$it::class" }}
    ]
)
class $className

""".trimIndent()

        moduleFile.writeText(text)
    }

    private fun File.toKtFile(): KtFile {
        val virtual = LightVirtualFile(name, KotlinFileType.INSTANCE, readText())
        return psiManager.findFile(virtual) as KtFile
    }


}