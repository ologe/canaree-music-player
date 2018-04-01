package dev.olog.lint.detectors

import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UImportStatement

class DomainDetector : ImportDetector(), Detector.UastScanner {

    companion object {
        val ISSUE : Issue = Issue.create(
                "DomainHelper",
                "Detect domain layer accessing outer layers",
                "See clean architecture",
                Category.CORRECTNESS,
                10,
                Severity.FATAL,
                Implementation(DomainDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun visitImport(context: JavaContext, uImportStatement: UImportStatement) {
        if (!isDomainLayer(context.packageName)){
            return
        }

        val importSource = uImportStatement.asSourceString()

        if (checkModelDependency(importSource)) {
            report(context, ISSUE, uImportStatement, "Domain layer cannot access model layer")
        }
        if (checkPresentationDependency(importSource)) {
            report(context, ISSUE, uImportStatement, "Domain layer cannot access presentation layer")
        }
    }

    private fun checkModelDependency(import: String): Boolean{
        return import.contains("$appPackage.data")
    }

    private fun checkPresentationDependency(import: String): Boolean {
        return import.contains("$appPackage.presentation")
    }

    private fun isDomainLayer(packageName: String): Boolean {
        return packageName.contains("$appPackage.domain")
    }

}