@file:Suppress("UnstableApiUsage")

package dev.olog.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import dev.olog.lint.performance.RecyclerViewPerformanceDetector
import dev.olog.lint.testing.ConcreteDispatcherDetector

class CustomLintRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = listOf(
            RecyclerViewPerformanceDetector.ISSUE,
            ConcreteDispatcherDetector.ISSUE
        )

    override val api: Int
        get() = CURRENT_API
}