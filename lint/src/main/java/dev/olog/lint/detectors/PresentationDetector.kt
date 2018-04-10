package dev.olog.lint.detectors

import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UImportStatement

class PresentationDetector : ImportDetector(), Detector.UastScanner {

    companion object {
        val ISSUE : Issue = Issue.create(
                "PresentationHelper",
                "Detect presentation layer accessing model layer",
                "See clean architecture",
                Category.CORRECTNESS,
                10,
                Severity.FATAL,
                Implementation(PresentationDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun visitImport(context: JavaContext, uImportStatement: UImportStatement) {
        if (!isPresentationLayer(context.packageName)){
            return
        }

        if (checkModelDependency(uImportStatement.asSourceString())){
            report(context, ISSUE, uImportStatement, "Presentation layer cannot access data layer")
        }

    }

    private fun checkModelDependency(import: String): Boolean{
        return import.contains("$appPackage.data.")
    }

    private fun isPresentationLayer(packageName: String): Boolean {
        return packageName.contains("$appPackage.presentation")
    }

}