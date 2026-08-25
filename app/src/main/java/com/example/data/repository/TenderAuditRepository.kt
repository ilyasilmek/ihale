package com.example.data.repository

import com.example.data.local.dao.TenderDao
import com.example.data.local.entity.AuditReportEntity
import com.example.data.local.entity.TenderDocumentEntity
import com.example.data.local.entity.TenderProjectEntity
import com.example.data.local.entity.UserRuleEntity
import com.example.domain.engine.AuditScoreCalculator
import com.example.domain.engine.CrossDocumentAuditEngine
import com.example.domain.engine.DocumentWithParams
import com.example.domain.engine.EkapAuditEngine
import com.example.domain.engine.LegislationRuleEngine
import com.example.domain.engine.SampleDataGenerator
import com.example.domain.engine.SampleTenderPackage
import com.example.domain.engine.SensitiveDataRedactionEngine
import com.example.domain.engine.TurkishLanguageEngine
import com.example.domain.model.AuditFinding
import com.example.domain.model.AuditScore
import com.example.domain.model.ConfidenceLevel
import com.example.domain.model.DocumentType
import com.example.domain.model.FindingCategory
import com.example.domain.model.Severity
import com.example.domain.model.TenderProcedure
import com.example.domain.model.TenderType
import com.example.domain.model.UserCustomRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

data class ProjectAuditResult(
    val project: TenderProjectEntity,
    val documents: List<TenderDocumentEntity>,
    val findings: List<AuditFinding>,
    val score: AuditScore
)

class TenderAuditRepository(private val tenderDao: TenderDao) {

    val allProjects: Flow<List<TenderProjectEntity>> = tenderDao.getAllProjects()
    val allUserRules: Flow<List<UserRuleEntity>> = tenderDao.getAllUserRules()
    val allReports: Flow<List<AuditReportEntity>> = tenderDao.getAllReports()

    fun getDocumentsForProject(projectId: String): Flow<List<TenderDocumentEntity>> {
        return tenderDao.getDocumentsForProject(projectId)
    }

    suspend fun createProject(
        tenderNumber: String,
        title: String,
        tenderType: TenderType,
        tenderProcedure: TenderProcedure,
        institutionName: String
    ): String = withContext(Dispatchers.IO) {
        val projectId = UUID.randomUUID().toString()
        val project = TenderProjectEntity(
            id = projectId,
            tenderNumber = tenderNumber,
            title = title,
            tenderType = tenderType,
            tenderProcedure = tenderProcedure,
            institutionName = institutionName
        )
        tenderDao.insertProject(project)
        projectId
    }

    suspend fun addDocumentToProject(
        projectId: String,
        name: String,
        type: DocumentType,
        content: String
    ) = withContext(Dispatchers.IO) {
        val words = content.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
        val pages = (content.length / 1500) + 1
        val doc = TenderDocumentEntity(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            name = name,
            documentType = type,
            rawContent = content,
            wordCount = words,
            pageCount = pages
        )
        tenderDao.insertDocument(doc)
    }

    suspend fun deleteDocument(documentId: String) = withContext(Dispatchers.IO) {
        tenderDao.deleteDocument(documentId)
    }

    suspend fun deleteProject(projectId: String) = withContext(Dispatchers.IO) {
        tenderDao.deleteDocumentsForProject(projectId)
        tenderDao.deleteProject(projectId)
    }

    suspend fun runProjectAudit(projectId: String): ProjectAuditResult? = withContext(Dispatchers.IO) {
        val project = tenderDao.getProjectById(projectId) ?: return@withContext null
        val documents = tenderDao.getDocumentsForProjectSync(projectId)
        val activeRules = tenderDao.getActiveUserRules()

        val allFindings = mutableListOf<AuditFinding>()
        val docsWithParams = mutableListOf<DocumentWithParams>()

        // 1. Single document audits for each document
        documents.forEach { doc ->
            val langFindings = TurkishLanguageEngine.analyze(doc.rawContent, doc.name, doc.documentType)
            val legFindings = LegislationRuleEngine.analyze(doc.rawContent, doc.name, doc.documentType)
            val sensitiveFindings = SensitiveDataRedactionEngine.generateFindings(doc.rawContent, doc.name, doc.documentType)
            val params = LegislationRuleEngine.extractParameters(doc.rawContent, doc.documentType)

            // Custom User Rules check
            val customFindings = checkCustomRules(doc.rawContent, doc.name, doc.documentType, activeRules)

            allFindings.addAll(langFindings)
            allFindings.addAll(legFindings)
            allFindings.addAll(sensitiveFindings)
            allFindings.addAll(customFindings)

            docsWithParams.add(
                DocumentWithParams(
                    id = doc.id,
                    name = doc.name,
                    type = doc.documentType,
                    content = doc.rawContent,
                    params = params
                )
            )
        }

        // 2. Cross-document comparison audit
        val crossFindings = CrossDocumentAuditEngine.performCrossAudit(docsWithParams)
        allFindings.addAll(crossFindings)

        // 3. EKAP Pre-check
        val ekapFindings = EkapAuditEngine.analyzeEkapReadiness(docsWithParams)
        allFindings.addAll(ekapFindings)

        // 4. Calculate final scores
        val score = AuditScoreCalculator.calculateScore(allFindings)

        ProjectAuditResult(
            project = project,
            documents = documents,
            findings = allFindings,
            score = score
        )
    }

    fun auditSingleText(
        content: String,
        documentName: String = "Belge",
        documentType: DocumentType = DocumentType.GENERAL_DOCUMENT
    ): Pair<List<AuditFinding>, AuditScore> {
        val findings = mutableListOf<AuditFinding>()
        findings.addAll(TurkishLanguageEngine.analyze(content, documentName, documentType))
        findings.addAll(LegislationRuleEngine.analyze(content, documentName, documentType))
        findings.addAll(SensitiveDataRedactionEngine.generateFindings(content, documentName, documentType))

        val score = AuditScoreCalculator.calculateScore(findings)
        return Pair(findings, score)
    }

    suspend fun loadSampleProject(sample: SampleTenderPackage): String = withContext(Dispatchers.IO) {
        val projectId = createProject(
            tenderNumber = sample.tenderNumber,
            title = sample.title,
            tenderType = sample.tenderType,
            tenderProcedure = sample.tenderProcedure,
            institutionName = sample.institutionName
        )
        sample.documents.forEach { doc ->
            addDocumentToProject(projectId, doc.name, doc.type, doc.content)
        }
        projectId
    }

    suspend fun initializeDefaultRulesIfEmpty() = withContext(Dispatchers.IO) {
        val rules = tenderDao.getActiveUserRules()
        if (rules.isEmpty()) {
            SampleDataGenerator.getDefaultUserRules().forEach { rule ->
                tenderDao.insertUserRule(
                    UserRuleEntity(
                        id = rule.id,
                        name = rule.name,
                        description = rule.description,
                        ruleType = rule.ruleType.name,
                        targetText = rule.targetText,
                        replacementText = rule.replacementText,
                        isEnabled = rule.isEnabled
                    )
                )
            }
        }
    }

    suspend fun saveUserRule(rule: UserCustomRule) = withContext(Dispatchers.IO) {
        tenderDao.insertUserRule(
            UserRuleEntity(
                id = rule.id,
                name = rule.name,
                description = rule.description,
                ruleType = rule.ruleType.name,
                targetText = rule.targetText,
                replacementText = rule.replacementText,
                isEnabled = rule.isEnabled
            )
        )
    }

    suspend fun deleteUserRule(ruleId: String) = withContext(Dispatchers.IO) {
        tenderDao.deleteUserRule(ruleId)
    }

    suspend fun saveAuditReport(report: AuditReportEntity) = withContext(Dispatchers.IO) {
        tenderDao.insertReport(report)
    }

    private fun checkCustomRules(
        content: String,
        documentName: String,
        documentType: DocumentType,
        rules: List<UserRuleEntity>
    ): List<AuditFinding> {
        val findings = mutableListOf<AuditFinding>()
        rules.forEach { rule ->
            if (rule.ruleType == "INSTITUTION_NAME_MUST_MATCH" && rule.targetText.isNotBlank()) {
                if (!content.contains(rule.targetText, ignoreCase = true) &&
                    (documentType == DocumentType.ADMINISTRATIVE_SPEC || documentType == DocumentType.TENDER_NOTICE)
                ) {
                    findings.add(
                        AuditFinding(
                            id = UUID.randomUUID().toString(),
                            ruleId = "RULE-CUSTOM-${rule.id}",
                            severity = Severity.WARNING,
                            category = FindingCategory.FORMAT_STRUCTURE,
                            title = "Özel Kurum Kuralı Uyuşmazlığı: \"${rule.name}\"",
                            description = "Tanımlı kurum kuralına göre belgede \"${rule.targetText}\" unvanının tam olarak yer alması gerekmektedir.",
                            detectedText = "Kurum adı tam eşleşmesi bulunamadı",
                            documentName = documentName,
                            documentType = documentType,
                            location = "Kurum Bilgileri",
                            suggestion = "Kurum adını \"${rule.targetText}\" olarak güncelleyiniz.",
                            confidence = ConfidenceLevel.HIGH
                        )
                    )
                }
            }
        }
        return findings
    }
}
