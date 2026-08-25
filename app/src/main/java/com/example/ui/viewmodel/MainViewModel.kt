package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.database.AppDatabase
import com.example.data.local.entity.AuditReportEntity
import com.example.data.local.entity.TenderDocumentEntity
import com.example.data.local.entity.TenderProjectEntity
import com.example.data.local.entity.UserRuleEntity
import com.example.data.repository.ProjectAuditResult
import com.example.data.repository.TenderAuditRepository
import com.example.domain.engine.SampleDataGenerator
import com.example.domain.engine.SampleTenderPackage
import com.example.domain.engine.SensitiveDataRedactionEngine
import com.example.domain.model.AuditFinding
import com.example.domain.model.AuditScore
import com.example.domain.model.CustomRuleType
import com.example.domain.model.DocumentType
import com.example.domain.model.FindingCategory
import com.example.domain.model.SensitiveDataItem
import com.example.domain.model.Severity
import com.example.domain.model.TenderProcedure
import com.example.domain.model.TenderType
import com.example.domain.model.UserCustomRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    HOME,
    SINGLE_DOC_AUDIT,
    TENDER_PROJECT_AUDIT,
    CROSS_COMPARE,
    CUSTOM_RULES,
    LEGISLATION_BROWSER,
    REPORTS_HISTORY
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TenderAuditRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TenderAuditRepository(db.tenderDao())
        viewModelScope.launch {
            repository.initializeDefaultRulesIfEmpty()
        }
    }

    // Navigation state
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Active project state
    private val _activeProjectId = MutableStateFlow<String?>(null)
    val activeProjectId: StateFlow<String?> = _activeProjectId.asStateFlow()

    // Projects list flow
    val allProjects: StateFlow<List<TenderProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active project audit result
    private val _projectAuditResult = MutableStateFlow<ProjectAuditResult?>(null)
    val projectAuditResult: StateFlow<ProjectAuditResult?> = _projectAuditResult.asStateFlow()

    // Active project documents
    private val _projectDocuments = MutableStateFlow<List<TenderDocumentEntity>>(emptyList())
    val projectDocuments: StateFlow<List<TenderDocumentEntity>> = _projectDocuments.asStateFlow()

    // Single Document Audit state
    private val _singleDocName = MutableStateFlow("Örnek_Şartname.txt")
    val singleDocName: StateFlow<String> = _singleDocName.asStateFlow()

    private val _singleDocType = MutableStateFlow(DocumentType.TECHNICAL_SPEC)
    val singleDocType: StateFlow<DocumentType> = _singleDocType.asStateFlow()

    private val _singleDocContent = MutableStateFlow("")
    val singleDocContent: StateFlow<String> = _singleDocContent.asStateFlow()

    private val _singleDocFindings = MutableStateFlow<List<AuditFinding>>(emptyList())
    val singleDocFindings: StateFlow<List<AuditFinding>> = _singleDocFindings.asStateFlow()

    private val _singleDocScore = MutableStateFlow<AuditScore?>(null)
    val singleDocScore: StateFlow<AuditScore?> = _singleDocScore.asStateFlow()

    private val _singleDocSensitiveData = MutableStateFlow<List<SensitiveDataItem>>(emptyList())
    val singleDocSensitiveData: StateFlow<List<SensitiveDataItem>> = _singleDocSensitiveData.asStateFlow()

    // Finding filter category
    private val _activeCategoryFilter = MutableStateFlow<FindingCategory?>(null)
    val activeCategoryFilter: StateFlow<FindingCategory?> = _activeCategoryFilter.asStateFlow()

    private val _activeSeverityFilter = MutableStateFlow<Severity?>(null)
    val activeSeverityFilter: StateFlow<Severity?> = _activeSeverityFilter.asStateFlow()

    // User Custom Rules
    val allUserRules: StateFlow<List<UserRuleEntity>> = repository.allUserRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Audit Reports History
    val allReports: StateFlow<List<AuditReportEntity>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Loading / Status Message
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Navigation methods
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setStatusMessage(msg: String?) {
        _statusMessage.value = msg
    }

    // Single document audit operations
    fun setSingleDocContent(text: String, name: String = _singleDocName.value, type: DocumentType = _singleDocType.value) {
        _singleDocContent.value = text
        _singleDocName.value = name
        _singleDocType.value = type
        runSingleDocAudit()
    }

    fun runSingleDocAudit() {
        val text = _singleDocContent.value
        if (text.isBlank()) {
            _singleDocFindings.value = emptyList()
            _singleDocScore.value = null
            _singleDocSensitiveData.value = emptyList()
            return
        }
        val (findings, score) = repository.auditSingleText(text, _singleDocName.value, _singleDocType.value)
        _singleDocFindings.value = findings
        _singleDocScore.value = score
        _singleDocSensitiveData.value = SensitiveDataRedactionEngine.findSensitiveData(text)
    }

    fun applySingleDocRedaction() {
        val current = _singleDocContent.value
        val items = _singleDocSensitiveData.value
        if (items.isNotEmpty()) {
            val redacted = SensitiveDataRedactionEngine.applyRedaction(current, items)
            _singleDocContent.value = redacted
            runSingleDocAudit()
            _statusMessage.value = "${items.size} adet hassas veri güvenli şekilde temizlendi."
        }
    }

    // Project operations
    fun selectProject(projectId: String) {
        _activeProjectId.value = projectId
        loadProjectDetails(projectId)
    }

    fun loadProjectDetails(projectId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.runProjectAudit(projectId)
            _projectAuditResult.value = result
            if (result != null) {
                _projectDocuments.value = result.documents
            }
            _isLoading.value = false
        }
    }

    fun createNewProject(
        tenderNumber: String,
        title: String,
        tenderType: TenderType,
        tenderProcedure: TenderProcedure,
        institutionName: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val id = repository.createProject(tenderNumber, title, tenderType, tenderProcedure, institutionName)
            selectProject(id)
            navigateTo(AppScreen.TENDER_PROJECT_AUDIT)
            _isLoading.value = false
            _statusMessage.value = "İhale dosyası oluşturuldu."
        }
    }

    fun addDocumentToActiveProject(name: String, type: DocumentType, content: String) {
        val pId = _activeProjectId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.addDocumentToProject(pId, name, type, content)
            loadProjectDetails(pId)
            _isLoading.value = false
            _statusMessage.value = "\"$name\" dosyası eklendi ve analiz edildi."
        }
    }

    fun deleteDocumentFromProject(documentId: String) {
        val pId = _activeProjectId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteDocument(documentId)
            loadProjectDetails(pId)
            _isLoading.value = false
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            if (_activeProjectId.value == projectId) {
                _activeProjectId.value = null
                _projectAuditResult.value = null
                navigateTo(AppScreen.HOME)
            }
            _statusMessage.value = "İhale dosyası silindi."
        }
    }

    fun loadSampleTenderProject(sample: SampleTenderPackage) {
        viewModelScope.launch {
            _isLoading.value = true
            val projectId = repository.loadSampleProject(sample)
            selectProject(projectId)
            navigateTo(AppScreen.TENDER_PROJECT_AUDIT)
            _isLoading.value = false
            _statusMessage.value = "Örnek ihale paketi başarıyla yüklendi ve denetlendi."
        }
    }

    fun loadSampleIntoSingleDoc() {
        val sampleText = """
            T.C. DEVLET DEMİRYOLLARI İŞLETMESİ GENEL MÜDÜRLÜĞÜ
            SİNYALİZASYON EKİPMANLARI TEKNİK ŞARTNAMESİ
            
            Madde 1 - Kapsam ve Donanım Standartları
            1.1. Sinyal kontrol istasyonları Siemens S7-1500 PLC kontrol ünitesi ile tam uyumlu olacaktır.
            1.2. Donanım teslimatı sırasında orjinal labaratuar onay raporları sunulmalıdır.
            1.3. Cihazlar 24 ay süreli üretici garantisine sahip olacaktır.
            
            Madde 2 - İhaleye Katılım ve Yeterlik Belgeleri
            2.1. İstekliler iş deneyim belgesi ve ISO 9001 kalite belgesini teklif aşamasında idareye sunmalıdır.
            2.2. İlgili personel T.C. Kimlik No: 12345678901 ve İletişim Tel: 0532 111 22 33'tür.
            2.3. Toplam 10 (oniki) adet sinyal modülü teslim edilecektir.
        """.trimIndent()
        setSingleDocContent(sampleText, "Demiryolu_Sinyal_Teknik_Sartname.txt", DocumentType.TECHNICAL_SPEC)
    }

    // Filter methods
    fun setCategoryFilter(category: FindingCategory?) {
        _activeCategoryFilter.value = category
    }

    fun setSeverityFilter(severity: Severity?) {
        _activeSeverityFilter.value = severity
    }

    // Custom Rules operations
    fun addCustomRule(
        name: String,
        description: String,
        type: CustomRuleType,
        targetText: String,
        replacementText: String?
    ) {
        viewModelScope.launch {
            val rule = UserCustomRule(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                ruleType = type,
                targetText = targetText,
                replacementText = replacementText,
                isEnabled = true
            )
            repository.saveUserRule(rule)
            _statusMessage.value = "Yeni kural kaydedildi."
            // Re-run active project audit if open
            _activeProjectId.value?.let { loadProjectDetails(it) }
        }
    }

    fun toggleRule(rule: UserRuleEntity) {
        viewModelScope.launch {
            val updated = UserCustomRule(
                id = rule.id,
                name = rule.name,
                description = rule.description,
                ruleType = CustomRuleType.valueOf(rule.ruleType),
                targetText = rule.targetText,
                replacementText = rule.replacementText,
                isEnabled = !rule.isEnabled
            )
            repository.saveUserRule(updated)
            _activeProjectId.value?.let { loadProjectDetails(it) }
        }
    }

    fun deleteRule(ruleId: String) {
        viewModelScope.launch {
            repository.deleteUserRule(ruleId)
            _activeProjectId.value?.let { loadProjectDetails(it) }
        }
    }

    // Save report to database
    fun saveCurrentProjectReport() {
        val result = _projectAuditResult.value ?: return
        viewModelScope.launch {
            val critCount = result.findings.count { it.severity == Severity.CRITICAL }
            val highCount = result.findings.count { it.severity == Severity.HIGH }
            val warnCount = result.findings.count { it.severity == Severity.WARNING }
            val sugCount = result.findings.count { it.severity == Severity.SUGGESTION }

            val report = AuditReportEntity(
                id = UUID.randomUUID().toString(),
                projectId = result.project.id,
                title = "${result.project.tenderNumber} - ${result.project.title}",
                overallScore = result.score.overallScore,
                criticalCount = critCount,
                highCount = highCount,
                warningCount = warnCount,
                suggestionCount = sugCount,
                reportJson = "İhale No: ${result.project.tenderNumber}\nSkor: ${result.score.overallScore}/100\nKritik: $critCount, Yüksek: $highCount"
            )
            repository.saveAuditReport(report)
            _statusMessage.value = "Ön kontrol raporu geçmişe kaydedildi."
        }
    }
}
