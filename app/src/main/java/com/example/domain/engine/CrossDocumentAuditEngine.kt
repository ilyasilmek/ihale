package com.example.domain.engine

import com.example.domain.model.AuditFinding
import com.example.domain.model.ConfidenceLevel
import com.example.domain.model.DocumentType
import com.example.domain.model.ExtractedParameters
import com.example.domain.model.FindingCategory
import com.example.domain.model.Severity
import java.util.UUID

data class DocumentWithParams(
    val id: String,
    val name: String,
    val type: DocumentType,
    val content: String,
    val params: ExtractedParameters
)

object CrossDocumentAuditEngine {

    fun performCrossAudit(documents: List<DocumentWithParams>): List<AuditFinding> {
        val findings = mutableListOf<AuditFinding>()

        val techSpec = documents.firstOrNull { it.type == DocumentType.TECHNICAL_SPEC }
        val adminSpec = documents.firstOrNull { it.type == DocumentType.ADMINISTRATIVE_SPEC }
        val contract = documents.firstOrNull { it.type == DocumentType.CONTRACT_DRAFT }
        val notice = documents.firstOrNull { it.type == DocumentType.TENDER_NOTICE }
        val schedule = documents.firstOrNull { it.type == DocumentType.PRICE_SCHEDULE }

        // 1. Garanti Süresi Çapraz Kontrolü (Teknik vs İdari vs Sözleşme)
        val warrantyMap = mutableMapOf<DocumentType, Int>()
        techSpec?.params?.warrantyMonths?.let { warrantyMap[DocumentType.TECHNICAL_SPEC] = it }
        adminSpec?.params?.warrantyMonths?.let { warrantyMap[DocumentType.ADMINISTRATIVE_SPEC] = it }
        contract?.params?.warrantyMonths?.let { warrantyMap[DocumentType.CONTRACT_DRAFT] = it }

        if (warrantyMap.size >= 2) {
            val distinctValues = warrantyMap.values.distinct()
            if (distinctValues.size > 1) {
                val details = warrantyMap.entries.joinToString(", ") { "${it.key.title}: ${it.value} Ay" }
                findings.add(
                    AuditFinding(
                        id = UUID.randomUUID().toString(),
                        ruleId = "RULE-CROSS-WARRANTY-MISMATCH",
                        severity = Severity.CRITICAL,
                        category = FindingCategory.CROSS_INCONSISTENCY,
                        title = "Kritik Garanti Süresi Çelişkisi",
                        description = "İhale dokümanları arasında farklı garanti süreleri tespit edilmiştir ($details). KİK ve yargı kararları uyarınca ihale dokümanları arasındaki çelişkiler esasa etkili aykırılık teşkil eder ve ihalenin iptaline neden olur.",
                        detectedText = details,
                        documentName = "Çapraz Doküman Denetimi",
                        documentType = DocumentType.TECHNICAL_SPEC,
                        location = "Teknik Şartname / İdari Şartname / Sözleşme Tasarısı",
                        legislationRef = "4734 Sayılı Kanun Madde 5 & KİK Tip Şartnameler Garanti Maddesi",
                        kikPrecedentRef = "KİK Kararı: 2023/UM.II-441 (Garanti süresinin dokümanlar arasında farklı olması ihalenin iptali sebebidir)",
                        suggestion = "Tüm belgelerdeki garanti sürelerini tek bir standart değerde (Örn. en kapsamlı olan süre esas alınarak) eşitleyiniz.",
                        confidence = ConfidenceLevel.HIGH
                    )
                )
            }
        }

        // 2. Teslim / İş Süresi Çapraz Kontrolü (İlan vs İdari vs Sözleşme)
        val deliveryMap = mutableMapOf<DocumentType, Int>()
        notice?.params?.deliveryDays?.let { deliveryMap[DocumentType.TENDER_NOTICE] = it }
        adminSpec?.params?.deliveryDays?.let { deliveryMap[DocumentType.ADMINISTRATIVE_SPEC] = it }
        contract?.params?.deliveryDays?.let { deliveryMap[DocumentType.CONTRACT_DRAFT] = it }

        if (deliveryMap.size >= 2) {
            val distinctValues = deliveryMap.values.distinct()
            if (distinctValues.size > 1) {
                val details = deliveryMap.entries.joinToString(", ") { "${it.key.title}: ${it.value} Gün" }
                findings.add(
                    AuditFinding(
                        id = UUID.randomUUID().toString(),
                        ruleId = "RULE-CROSS-DELIVERY-MISMATCH",
                        severity = Severity.CRITICAL,
                        category = FindingCategory.CROSS_INCONSISTENCY,
                        title = "Kritik Teslim Süresi Uyuşmazlığı",
                        description = "İhale İlanı, İdari Şartname ve Sözleşme Tasarısı arasında işin teslim süresine ilişkin çelişki tespit edilmiştir ($details). İsteklilerin teklif hazırlama ve maliyet hesabını doğrudan etkiler.",
                        detectedText = details,
                        documentName = "Çapraz Doküman Denetimi",
                        documentType = DocumentType.ADMINISTRATIVE_SPEC,
                        location = "İlan / İdari Şartname / Sözleşme",
                        legislationRef = "4734 Sayılı Kanun Madde 24 & Tip İdari Şartname",
                        kikPrecedentRef = "KİK Kararı: 2024/UY.I-320 (İlan ve İdari şartnamedeki süre çelişkisi)",
                        suggestion = "İlan metnindeki teslim günü ile şartname ve sözleşme tasarısındaki teslim gününü tam olarak eşitleyiniz.",
                        confidence = ConfidenceLevel.HIGH
                    )
                )
            }
        }

        // 3. Birim Fiyat Teklif Cetveli ile Teknik Şartname Kalem Sayısı Uyuşmazlığı
        val techItems = techSpec?.params?.totalScheduleItems
        val scheduleItems = schedule?.params?.totalScheduleItems

        if (techItems != null && scheduleItems != null && techItems != scheduleItems) {
            findings.add(
                AuditFinding(
                    id = UUID.randomUUID().toString(),
                    ruleId = "RULE-CROSS-SCHEDULE-COUNT-MISMATCH",
                    severity = Severity.CRITICAL,
                    category = FindingCategory.PRICE_SCHEDULE,
                    title = "Teknik Şartname ile Teklif Cetveli İş Kalemi Sayısı Uyuşmazlığı",
                    description = "Teknik Şartnamede $techItems adet iş kalemi tanımlanmışken, Birim Fiyat Teklif Cetvelinde $scheduleItems adet kalem tespit edilmiştir. Kalem atlaması veya eksik tarif tekliflerin geçersiz sayılmasına veya idari iptale yol açar.",
                    detectedText = "Teknik Şartname: $techItems Kalem | Teklif Cetveli: $scheduleItems Kalem",
                    documentName = schedule?.name ?: "Birim Fiyat Teklif Cetveli",
                    documentType = DocumentType.PRICE_SCHEDULE,
                    location = "Teklif Cetveli Tablosu",
                    legislationRef = "4734 Sayılı Kanun Madde 27 & Tip Şartnameler",
                    kikPrecedentRef = "KİK Kararı: 2023/UM.I-915 (Cetvelde yer almayan kalemin şartnamede bulunması)",
                    suggestion = "Teknik şartnamedeki her bir teknik iş kaleminin Teklif Cetvelinde birebir aynı sıra no ve adlandırma ile yer aldığından emin olunuz.",
                    confidence = ConfidenceLevel.HIGH
                )
            )
        }

        // 4. Cezai Şartlar Tutarlılığı (İdari Şartname vs Sözleşme Tasarısı)
        val adminPenalty = adminSpec?.params?.penaltyRatePerDay
        val contractPenalty = contract?.params?.penaltyRatePerDay

        if (adminPenalty != null && contractPenalty != null && adminPenalty != contractPenalty) {
            findings.add(
                AuditFinding(
                    id = UUID.randomUUID().toString(),
                    ruleId = "RULE-CROSS-PENALTY-MISMATCH",
                    severity = Severity.HIGH,
                    category = FindingCategory.CROSS_INCONSISTENCY,
                    title = "Gecikme Cezası Oranı Uyuşmazlığı",
                    description = "İdari Şartnamede günlük gecikme cezası %$adminPenalty olarak belirlenmişken, Sözleşme Tasarısında %$contractPenalty olarak yazılmıştır.",
                    detectedText = "İdari Şartname: %$adminPenalty | Sözleşme: %$contractPenalty",
                    documentName = contract?.name ?: "Sözleşme Tasarısı",
                    documentType = DocumentType.CONTRACT_DRAFT,
                    location = "Cezalar Maddesi",
                    legislationRef = "4735 Sayılı Kanun Madde 6",
                    suggestion = "İdari şartname ve sözleşme tasarısındaki gecikme cezası oranlarını aynı değere getiriniz.",
                    confidence = ConfidenceLevel.HIGH
                )
            )
        }

        return findings
    }
}
