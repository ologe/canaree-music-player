@file:Suppress("unused")

package dev.olog.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue
import dev.olog.lint.detectors.DataDetector
import dev.olog.lint.detectors.DomainDetector
import dev.olog.lint.detectors.PresentationDetector

class CustomIssueRegistry : IssueRegistry() {

    override fun getIssues(): MutableList<Issue> {
        return mutableListOf(
                PresentationDetector.ISSUE,
                DomainDetector.ISSUE,
                DataDetector.ISSUE
        )
    }
}