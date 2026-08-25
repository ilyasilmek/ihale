package com.example.domain.engine

import com.example.domain.model.AuditFinding
import com.example.domain.model.ConfidenceLevel
import com.example.domain.model.DocumentType
import com.example.domain.model.FindingCategory
import com.example.domain.model.Severity
import java.util.UUID

object EkapAuditEngine {

    fun analyzeEkapReadiness(documents: List<DocumentWithParams>): List<AuditFinding> {
        val findings = mutableListOf<AuditFinding>()

        val adminSpec = documents.firstOrNull { it.type == DocumentType.ADMINISTRATIVE_SPEC }
        val adminContent = adminSpec?.content ?: ""

        // 1. E-İhale / E-Anahtar / E-İmza Zorunluluğu Kontrolü
        if (adminContent.isNotEmpty()) {
            val hasEkapMention = adminContent.contains("EKAP", ignoreCase = true)
            val hasEKeyMention = adminContent.contains("e-anahtar", ignoreCase = true) ||
                    adminContent.contains("elektronik imza", ignoreCase = true) ||
                    adminContent.contains("e-imza", ignoreCase = true)

            if (!hasEkapMention) {
                findings.add(
                    AuditFinding(
                        id = UUID.randomUUID().toString(),
                        ruleId = "RULE-EKAP-MISSING-REF",
                        severity = Severity.HIGH,
                        category = FindingCategory.EKAP_PRECHECK,
                        title = "İdari Şartnamede EKAP Elektronik İhale Süreçleri Eksik",
                        description = "Elektronik Kamu Alımları Platformu (EKAP) üzerinden yapılan ihalelerde tip idari şartnamede e-teklif, e-anahtar ve e-imza kullanımı hükümlerine yer verilmesi zorunludur.",
                        detectedText = "EKAP ve E-İmza atıfları bulunamadı",
                        documentName = adminSpec?.name ?: "İdari Şartname",
                        documentType = DocumentType.ADMINISTRATIVE_SPEC,
                        location = "İdari Şartname Genel Hükümler",
                        legislationRef = "Elektronik İhale Uygulama Yönetmeliği Madde 4 & 5",
                        suggestion = "EKAP Tip İdari Şartname e-teklif hazırlama ve şifreleme (e-anahtar) maddelerini dokümana dahil ediniz.",
                        confidence = ConfidenceLevel.HIGH
                    )
                )
            } else if (!hasEKeyMention) {
                findings.add(
                    AuditFinding(
                        id = UUID.randomUUID().toString(),
                        ruleId = "RULE-EKAP-EKEY-MISSING",
                        severity = Severity.WARNING,
                        category = FindingCategory.EKAP_PRECHECK,
                        title = "E-Anahtar ve E-Teklif Şifreleme İfadesi Eksik",
                        description = "EKAP üzerinden alınan e-tekliflerde isteklilerin tekliflerini e-anahtar ile şifrelemeleri ve açılış saatinde e-anahtarlarını göndermeleri kuralı şartnamede yer almalıdır.",
                        detectedText = "e-anahtar ibaresi eksik",
                        documentName = adminSpec?.name ?: "İdari Şartname",
                        documentType = DocumentType.ADMINISTRATIVE_SPEC,
                        location = "Tekliflerin Sunulması Maddesi",
                        legislationRef = "Elektronik İhale Uygulama Yönetmeliği Madde 20",
                        suggestion = "Tekliflerin şifrelenmesi ve e-anahtarların EKAP'a iletilmesi ile ilgili standart düzenlemeyi ekleyiniz.",
                        confidence = ConfidenceLevel.MEDIUM
                    )
                )
            }
        }

        // 2. Yeterlik Bilgileri Tablosu (YBT) Kontrolü
        val hasYbt = documents.any { it.type == DocumentType.ELIGIBILITY_TABLE } ||
                adminContent.contains("yeterlik bilgileri tablosu", ignoreCase = true)

        if (!hasYbt && adminContent.isNotEmpty()) {
            findings.add(
                AuditFinding(
                    id = UUID.randomUUID().toString(),
                    ruleId = "RULE-EKAP-YBT-CHECK",
                    severity = Severity.WARNING,
                    category = FindingCategory.EKAP_PRECHECK,
                    title = "Yeterlik Bilgileri Tablosu Düzenlemesi Görünmüyor",
                    description = "E-ihalelerde istekliler fiziksel belge yerine EKAP üzerinde 'Yeterlik Bilgileri Tablosu'nu doldurarak katılım sağlarlar. İdari şartnamede bu tabloya ilişkin açıklamaların bulunması gereklidir.",
                    detectedText = "Yeterlik Bilgileri Tablosu atfı bulunamadı",
                    documentName = adminSpec?.name ?: "İhale Dosyası",
                    documentType = DocumentType.ADMINISTRATIVE_SPEC,
                    location = "Yeterlik Belgeleri Maddesi",
                    legislationRef = "Elektronik İhale Uygulama Yönetmeliği Madde 21",
                    suggestion = "İdari şartnamenin yeterlik maddesinde Yeterlik Bilgileri Tablosu formatının esas alınacağını belirtiniz.",
                    confidence = ConfidenceLevel.HIGH
                )
            )
        }

        return findings
    }
}
