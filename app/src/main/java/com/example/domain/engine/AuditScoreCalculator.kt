package com.example.domain.engine

import com.example.domain.model.AuditFinding
import com.example.domain.model.AuditScore
import com.example.domain.model.FindingCategory
import com.example.domain.model.Severity
import kotlin.math.max

object AuditScoreCalculator {

    fun calculateScore(findings: List<AuditFinding>): AuditScore {
        // Base score starts at 100
        var legislationDeduction = 0
        var crossDeduction = 0
        var brandDeduction = 0
        var eligibilityDeduction = 0
        var languageDeduction = 0
        var ekapDeduction = 0

        findings.forEach { finding ->
            val points = when (finding.severity) {
                Severity.CRITICAL -> 15
                Severity.HIGH -> 10
                Severity.WARNING -> 5
                Severity.SUGGESTION -> 2
                Severity.SUCCESS -> 0
            }

            when (finding.category) {
                FindingCategory.LEGISLATION -> legislationDeduction += points
                FindingCategory.CROSS_INCONSISTENCY -> crossDeduction += points
                FindingCategory.BRAND_RISK -> brandDeduction += points
                FindingCategory.ELIGIBILITY -> eligibilityDeduction += points
                FindingCategory.PRICE_SCHEDULE -> crossDeduction += points
                FindingCategory.EKAP_PRECHECK -> ekapDeduction += points
                FindingCategory.TURKISH_LANGUAGE,
                FindingCategory.FORMAT_STRUCTURE -> languageDeduction += points
                FindingCategory.SENSITIVE_DATA -> legislationDeduction += points
            }
        }

        val legScore = max(0, 100 - legislationDeduction)
        val crossScore = max(0, 100 - crossDeduction)
        val brandScore = max(0, 100 - brandDeduction)
        val eligScore = max(0, 100 - eligibilityDeduction)
        val langScore = max(0, 100 - languageDeduction)
        val ekScore = max(0, 100 - ekapDeduction)

        // Weighted Overall Score
        val overall = (
                legScore * 0.25 +
                        crossScore * 0.25 +
                        brandScore * 0.20 +
                        eligScore * 0.10 +
                        ekScore * 0.10 +
                        langScore * 0.10
                ).toInt()

        return AuditScore(
            overallScore = overall,
            legislationScore = legScore,
            crossConsistencyScore = crossScore,
            brandRiskScore = brandScore,
            eligibilityScore = eligScore,
            languageScore = langScore,
            ekapScore = ekScore
        )
    }
}
