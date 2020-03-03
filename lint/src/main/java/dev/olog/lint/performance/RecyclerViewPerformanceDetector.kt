@file:Suppress("UnstableApiUsage")

package dev.olog.lint.performance

import com.android.SdkConstants
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.LayoutDetector
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.XmlContext
import org.w3c.dom.Element
import org.w3c.dom.Node

class RecyclerViewPerformanceDetector : LayoutDetector() {

    companion object {
        private val EXPLANATION = """
            NestedScrollView shouldn't be parent/ancestor of RecyclerView.
            For some reason RecyclerView has to lay out all it's children when has a NestedScrollView parent/ancestor.
            https://gph.is/2cI3chF
        """.trimIndent()

        val ISSUE: Issue = Issue.create(
            "RecyclerViewPerformance",
            "NestedScrollView shouldn't be parent/ancestor of RecyclerView",
            EXPLANATION,
            Category.PERFORMANCE,
            10,
            Severity.WARNING,
            Implementation(
                RecyclerViewPerformanceDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )
    }

    override fun getApplicableElements(): Collection<String> {
        return listOf(
            SdkConstants.RECYCLER_VIEW.newName(),
            SdkConstants.RECYCLER_VIEW.oldName()
        )
    }

    override fun visitElement(
        context: XmlContext,
        element: Element
    ) {
        val parentNode = element.parentNode ?: return
        searchForNestedScrollViewAncestors(context, element, parentNode)
    }

    private fun searchForNestedScrollViewAncestors(
        context: XmlContext,
        element: Element,
        parentNode: Node
    ) {
        var node = parentNode
        while (node.nodeType == Node.ELEMENT_NODE) {
            // not root node

            val current = node as Element
            if (SdkConstants.NESTED_SCROLL_VIEW.isEquals(current.tagName)) {
                // parent nested scroll view found, report and exit
                report(context, element)
                return
            }
            node = node.getParentNode()
        }
    }

    private fun report(
        context: XmlContext,
        element: Element
    ) {
        context.report(ISSUE, context.getLocation(element), EXPLANATION)
    }
}