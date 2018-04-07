package dev.olog.lint.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UImportStatement

abstract class ImportDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): MutableList<Class<out UElement>> {
        return mutableListOf(UImportStatement::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitImportStatement(uImportStatement: UImportStatement) {
                visitImport(context, uImportStatement)
            }
        }
    }

    protected abstract fun visitImport(context: JavaContext, uImportStatement: UImportStatement)

    protected fun report(context: JavaContext, issue: Issue, uImportStatement: UImportStatement, message: String){
        val fix = fix()

                .replace()
                .text(uImportStatement.asSourceString())
                .reformat(true)
                .with("")
                .build()

        context.report(issue, uImportStatement, context.getLocation(uImportStatement), message, fix)
    }

    protected val JavaContext.packageName: String
        get() = this.uastFile!!.packageName

    protected val appPackage: String
        get() = "dev.olog.msc"

}