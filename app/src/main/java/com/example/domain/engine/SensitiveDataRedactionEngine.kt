package com.example.domain.engine

import com.example.domain.model.AuditFinding
import com.example.domain.model.ConfidenceLevel
import com.example.domain.model.DocumentType
import com.example.domain.model.FindingCategory
import com.example.domain.model.SensitiveDataItem
import com.example.domain.model.SensitiveDataType
import com.example.domain.model.Severity
import java.util.UUID

object SensitiveDataRedactionEngine {

    // TC Kimlik No regex (11 digits, starts with non-zero)
    private val tcRegex = Regex("\\b([1-9]\\d{10})\\b")
    
    // Turkish IBAN regex (TR + 24 digits, optional spaces)
    private val ibanRegex = Regex("\\bTR\\d{2}\\s?(?:\\d{4}\\s?){5}\\d{2}\\b", RegexOption.IGNORE_CASE)
    
    // Turkish GSM Phone regex (05xx xxx xx xx or +90 5xx ...)
    private val phoneRegex = Regex("(?:\\+90|0)?\\s?5\\d{2}[\\s.-]?\\d{3}[\\s.-]?\\d{2}[\\s.-]?\\d{2}\\b")
    
    // Email regex
    private val emailRegex = Regex("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,7}\\b")

    fun findSensitiveData(content: String): List<SensitiveDataItem> {
        val items = mutableListOf<SensitiveDataItem>()

        // 1. TC Kimlik No with Luhn Check
        tcRegex.findAll(content).forEach { match ->
            val tc = match.value
            if (isValidTurkishId(tc)) {
                items.add(
                    SensitiveDataItem(
                        id = UUID.randomUUID().toString(),
                        type = SensitiveDataType.TC_KIMLIK,
                        originalText = tc,
                        maskedText = "${tc.take(3)}*****${tc.takeLast(2)}",
                        location = "Karakter: ${match.range.first}-${match.range.last}"
                    )
                )
            }
        }

        // 2. IBAN
        ibanRegex.findAll(content).forEach { match ->
            val iban = match.value
            items.add(
                SensitiveDataItem(
                    id = UUID.randomUUID().toString(),
                    type = SensitiveDataType.IBAN,
                    originalText = iban,
                    maskedText = "TR** **** **** **** **** ${iban.takeLast(4)}",
                    location = "Karakter: ${match.range.first}-${match.range.last}"
                )
            )
        }

        // 3. Phone
        phoneRegex.findAll(content).forEach { match ->
            val phone = match.value
            items.add(
                SensitiveDataItem(
                    id = UUID.randomUUID().toString(),
                    type = SensitiveDataType.PHONE,
                    originalText = phone,
                    maskedText = "${phone.take(4)} *** ** ${phone.takeLast(2)}",
                    location = "Karakter: ${match.range.first}-${match.range.last}"
                )
            )
        }

        // 4. Email
        emailRegex.findAll(content).forEach { match ->
            val email = match.value
            val parts = email.split("@")
            val masked = if (parts.size == 2 && parts[0].length > 2) {
                "${parts[0].take(2)}***@${parts[1]}"
            } else {
                "***@***"
            }
            items.add(
                SensitiveDataItem(
                    id = UUID.randomUUID().toString(),
                    type = SensitiveDataType.EMAIL,
                    originalText = email,
                    maskedText = masked,
                    location = "Karakter: ${match.range.first}-${match.range.last}"
                )
            )
        }

        return items
    }

    fun generateFindings(content: String, documentName: String, documentType: DocumentType): List<AuditFinding> {
        val sensitiveItems = findSensitiveData(content)
        return sensitiveItems.map { item ->
            AuditFinding(
                id = UUID.randomUUID().toString(),
                ruleId = "RULE-KVKK-SENSITIVE",
                severity = Severity.WARNING,
                category = FindingCategory.SENSITIVE_DATA,
                title = "Hassas Kişisel Veri / KVKK Riski: ${item.type.label}",
                description = "Dokümanda açık olarak tespit edilen ${item.type.label} (\"${item.originalText}\") kamuya açık ihale ilanlarında veya EKAP dokümanlarında kişisel verilerin korunması kanununa aykırılık teşkil edebilir.",
                detectedText = item.originalText,
                documentName = documentName,
                documentType = documentType,
                location = item.location,
                legislationRef = "6698 Sayılı Kişisel Verilerin Korunması Kanunu (KVKK)",
                suggestion = "İlgili veriyi maskeleyiniz (Örnek: \"${item.maskedText}\") veya dokümandan kaldırınız.",
                confidence = ConfidenceLevel.HIGH
            )
        }
    }

    fun applyRedaction(content: String, itemsToRedact: List<SensitiveDataItem>): String {
        var redactedContent = content
        itemsToRedact.forEach { item ->
            redactedContent = redactedContent.replace(item.originalText, "[KİŞİSEL VERİ GİZLENDİ - ${item.type.name}]")
        }
        return redactedContent
    }

    private fun isValidTurkishId(tc: String): Boolean {
        if (tc.length != 11 || tc.startsWith("0")) return false
        val digits = tc.map { it.toString().toIntOrNull() ?: return false }

        // 1, 3, 5, 7, 9. haneler toplamı * 7 - (2, 4, 6, 8. haneler toplamı) mod 10 == 10. hane
        val oddSum = digits[0] + digits[2] + digits[4] + digits[6] + digits[8]
        val evenSum = digits[1] + digits[3] + digits[5] + digits[7]
        val tenth = ((oddSum * 7) - evenSum) % 10
        val posTenth = if (tenth < 0) tenth + 10 else tenth

        if (posTenth != digits[9]) return false

        // İlk 10 hanenin toplamının mod 10'u == 11. hane
        val first10Sum = digits.take(10).sum()
        if (first10Sum % 10 != digits[10]) return false

        return true
    }
}
