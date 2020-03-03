@file:Suppress("UnstableApiUsage")

package dev.olog.lint.testing

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement

/**
 * Very dummy linter that checks for usages of [Visitor.dispatchers]
 */
class ConcreteDispatcherDetector : Detector(), SourceCodeScanner {

    companion object {
        const val EXPLANATION = """Get a dispatcher from a scheduler factory instead of using a concrete one"""

        // this won't break the build
        private val SEVERITY = Severity.FATAL

        val ISSUE: Issue = Issue.create(
            "ConcreteDispatcherIssue",
            EXPLANATION,
            EXPLANATION,
            Category.TESTING,
            10,
            SEVERITY,
            Implementation(
                ConcreteDispatcherDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(
            UCallExpression::class.java
        )
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return Visitor(context)
    }
}

private class Visitor(
    private val context: JavaContext
) : UElementHandler() {

    companion object {
        private val dispatchers = listOf(
            "Dispatchers.IO",
            "Dispatchers.Default",
            "Dispatchers.Main",
            "Dispatchers.Unconfined"
        )
    }

    override fun visitCallExpression(node: UCallExpression) {

        if (node.valueArguments.any { it.asSourceString() in dispatchers }) {
            val location = context.getLocation(node)
            context.report(
                ConcreteDispatcherDetector.ISSUE,
                location,
                ConcreteDispatcherDetector.EXPLANATION
            )
        }
    }
}