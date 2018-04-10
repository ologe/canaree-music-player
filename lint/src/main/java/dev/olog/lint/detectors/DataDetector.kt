package dev.olog.lint.detectors

import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UImportStatement

class DataDetector : ImportDetector(), Detector.UastScanner {

    companion object {
        val ISSUE : Issue = Issue.create(
                "DataHelper",
                "Detect domain accessing model layer",
                "See clean architecture",
                Category.CORRECTNESS,
                10,
                Severity.FATAL,
                Implementation(DataDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun visitImport(context: JavaContext, uImportStatement: UImportStatement) {
        if (!isDataLayer(context.packageName)){
            return
        }

        if (checkPresentationLayerDependency(uImportStatement.asSourceString())){
            report(context, ISSUE, uImportStatement, "Data layer cannot access presentation layer")
        }

    }

    private fun checkPresentationLayerDependency(import: String): Boolean {
        return import.contains("$appPackage.presentation.")
    }

    private fun isDataLayer(packageName: String): Boolean {
        return packageName.contains("$appPackage.data")
    }

}