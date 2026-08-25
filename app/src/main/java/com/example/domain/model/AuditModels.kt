package com.example.domain.model

enum class Severity(val label: String, val badge: String) {
    CRITICAL("Kritik", "🔴"),
    HIGH("Yüksek Risk", "🟠"),
    WARNING("Dikkat", "🟡"),
    SUGGESTION("Öneri", "🔵"),
    SUCCESS("Uygun", "🟢")
}

enum class FindingCategory(val displayName: String) {
    LEGISLATION("Mevzuat Uyumu"),
    CROSS_INCONSISTENCY("Belgeler Arası Çelişki"),
    BRAND_RISK("Marka / Model / Rekabet Riski"),
    ELIGIBILITY("Yeterlik Kriteri Ayrımı"),
    PRICE_SCHEDULE("Teklif Cetveli / Miktar"),
    EKAP_PRECHECK("EKAP Ön Kontrolü"),
    TURKISH_LANGUAGE("Türkçe & Resmi Yazışma"),
    FORMAT_STRUCTURE("Biçim & Numaralandırma"),
    SENSITIVE_DATA("KVKK / Hassas Veri")
}

enum class ConfidenceLevel(val label: String) {
    HIGH("Yüksek Güven (%95+)"),
    MEDIUM("Orta Güven (%75+)"),
    LOW("Düşük Güven (%50+ - İnceleme Gerekli)")
}

enum class DocumentType(val title: String, val shortCode: String) {
    TECHNICAL_SPEC("Teknik Şartname", "TŞ"),
    ADMINISTRATIVE_SPEC("İdari Şartname", "İŞ"),
    CONTRACT_DRAFT("Sözleşme Tasarısı", "ST"),
    PRICE_SCHEDULE("Birim Fiyat Teklif Cetveli", "BFTC"),
    TENDER_NOTICE("İhale İlanı", "İLAN"),
    ELIGIBILITY_TABLE("Yeterlik Bilgileri Tablosu", "YBT"),
    STANDARD_FORM("Standart Form", "SF"),
    GENERAL_DOCUMENT("Genel Belge", "GB")
}

enum class TenderType(val displayName: String) {
    GOODS("Mal Alımı"),
    SERVICES("Hizmet Alımı"),
    WORKS("Yapım İşi"),
    CONSULTING("Danışmanlık Hizmeti")
}

enum class TenderProcedure(val displayName: String) {
    OPEN("Açık İhale Usulü (Md. 19)"),
    RESTRICTED("Belli İstekliler Arasında (Md. 20)"),
    NEGOTIATED("Pazarlık Usulü (Md. 21)"),
    DIRECT_PROCUREMENT("Doğrudan Temin (Md. 22)"),
    E_TENDER("Elektronik İhale (EKAP)")
}

data class ExtractedParameters(
    val quantities: Map<String, Double> = emptyMap(), // Item -> Qty
    val warrantyMonths: Int? = null,
    val deliveryDays: Int? = null,
    val penaltyRatePerDay: Double? = null, // e.g. 0.05%
    val bidBondPercent: Double? = null, // e.g. 3%
    val performanceBondPercent: Double? = null, // e.g. 6%
    val mentionedBrands: List<String> = emptyList(),
    val missingEquivalentBrands: List<String> = emptyList(),
    val extractedDates: List<String> = emptyList(),
    val eligibilityClausesInTechSpec: List<String> = emptyList(),
    val totalScheduleItems: Int? = null
)

data class AuditFinding(
    val id: String,
    val ruleId: String,
    val severity: Severity,
    val category: FindingCategory,
    val title: String,
    val description: String,
    val detectedText: String,
    val documentName: String,
    val documentType: DocumentType,
    val location: String, // e.g., "Madde 4.2 / Paragraf 3"
    val legislationRef: String? = null, // e.g., "4734 Sayılı Kanun Madde 12"
    val kikPrecedentRef: String? = null, // e.g., "KİK Kararı: 2024/UH.II-892"
    val suggestion: String,
    val confidence: ConfidenceLevel = ConfidenceLevel.HIGH,
    val isAccepted: Boolean? = null // null: pending, true: accepted, false: rejected
)

data class AuditScore(
    val overallScore: Int,
    val legislationScore: Int,
    val crossConsistencyScore: Int,
    val brandRiskScore: Int,
    val eligibilityScore: Int,
    val languageScore: Int,
    val ekapScore: Int
)

data class UserCustomRule(
    val id: String,
    val name: String,
    val description: String,
    val ruleType: CustomRuleType,
    val targetText: String,
    val replacementText: String? = null,
    val isEnabled: Boolean = true
)

enum class CustomRuleType {
    INSTITUTION_NAME_MUST_MATCH,
    DATE_FORMAT_MANDATE,
    SPELLING_REPLACE,
    FORBIDDEN_KEYWORD
}

data class SensitiveDataItem(
    val id: String,
    val type: SensitiveDataType,
    val originalText: String,
    val maskedText: String,
    val location: String,
    val isRedacted: Boolean = true
)

enum class SensitiveDataType(val label: String) {
    TC_KIMLIK("T.C. Kimlik Numarası"),
    IBAN("Banka Hesap / IBAN"),
    PHONE("Telefon Numarası"),
    EMAIL("E-Posta Adresi"),
    REGISTRATION_NO("Sicil / Vergi No")
}
