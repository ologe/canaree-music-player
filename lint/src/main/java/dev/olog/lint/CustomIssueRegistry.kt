@file:Suppress("unused")

package dev.olog.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import dev.olog.lint.detectors.DataDetector
import dev.olog.lint.detectors.DomainDetector
import dev.olog.lint.detectors.PresentationDetector

class CustomIssueRegistry : IssueRegistry() {

    override val api: Int
        get() = CURRENT_API

    override val issues: List<Issue>
        get() {
            return mutableListOf(
                    PresentationDetector.ISSUE,
                    DomainDetector.ISSUE,
                    DataDetector.ISSUE
            )
        }
}