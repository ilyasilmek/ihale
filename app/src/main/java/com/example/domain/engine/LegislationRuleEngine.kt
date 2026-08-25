package com.example.domain.engine

import com.example.domain.model.AuditFinding
import com.example.domain.model.ConfidenceLevel
import com.example.domain.model.DocumentType
import com.example.domain.model.ExtractedParameters
import com.example.domain.model.FindingCategory
import com.example.domain.model.Severity
import java.util.UUID

object LegislationRuleEngine {

    // Common brand / manufacturer names often accidentally placed in Turkish technical specs
    private val knownBrands = listOf(
        "Cisco", "Siemens", "Oracle", "Microsoft", "Intel", "AMD", "Dell", "HP", "Huawei", "Apple",
        "Bosch", "Schneider", "Philips", "ABB", "Komatsu", "Caterpillar", "CAT", "3M", "Cif", "Hilti",
        "Samsung", "Lenovo", "Sony", "Grundfos", "Danfoss", "Knauf", "Jotun", "Filli Boya", "Pirelli",
        "Mercedes", "Ford", "Toyota", "Volkswagen", "Epson", "Canon", "Honeywell", "Fortinet"
    )

    // Keywords signaling qualification documents wrongfully placed in Technical Specs
    private val eligibilityKeywordsInTechSpec = listOf(
        "iş deneyim belgesi",
        "iş bitirme belgesi",
        "mali yeterlik",
        "banka referans mektubu",
        "bilanço ve gelir tablosu",
        "ticaret ve sanayi odası belgesi",
        "oda sicil kaydı",
        "imza sirküleri",
        "isteklinin yetkili satıcı belgesi",
        "üretici yetki belgesi teklif zarfında",
        "iso 9001 belgesi teklif aşamasında"
    )

    fun extractParameters(content: String, documentType: DocumentType): ExtractedParameters {
        // Extract warranty
        val warrantyRegex = Regex("(\\d+)\\s*(?:\\([^)]+\\))?\\s*(?:ay|yıl)\\s*(?:süreli)?\\s*garanti", RegexOption.IGNORE_CASE)
        val warrantyMatch = warrantyRegex.find(content)
        val warrantyMonths = warrantyMatch?.let {
            val num = it.groupValues[1].toIntOrNull() ?: 0
            if (it.value.contains("yıl", ignoreCase = true)) num * 12 else num
        }

        // Extract delivery time
        val deliveryRegex = Regex("(\\d+)\\s*(?:\\([^)]+\\))?\\s*(?:takvim)?\\s*gün\\s*(?:içinde|içerisinde|teslim)", RegexOption.IGNORE_CASE)
        val deliveryMatch = deliveryRegex.find(content)
        val deliveryDays = deliveryMatch?.let { it.groupValues[1].toIntOrNull() }

        // Extract penalty rate
        val penaltyRegex = Regex("%\\s*(\\d+[.,]?\\d*)\\s*(?:gecikme cezası|oranında ceza|ceza kesilir)", RegexOption.IGNORE_CASE)
        val penaltyMatch = penaltyRegex.find(content)
        val penaltyRate = penaltyMatch?.let {
            it.groupValues[1].replace(',', '.').toDoubleOrNull()
        }

        // Extract brand names mentioned
        val mentioned = mutableListOf<String>()
        val missingEquivalent = mutableListOf<String>()
        val sentences = content.split(".", ";", "\n")

        knownBrands.forEach { brand ->
            val brandRegex = Regex("\\b(?i)$brand\\b")
            sentences.forEach { sentence ->
                if (brandRegex.containsMatchIn(sentence)) {
                    if (!mentioned.contains(brand)) mentioned.add(brand)
                    val hasEquivalent = sentence.contains("veya dengi", ignoreCase = true) ||
                            sentence.contains("veya muadili", ignoreCase = true) ||
                            sentence.contains("veya eşdeğeri", ignoreCase = true)
                    if (!hasEquivalent && !missingEquivalent.contains(brand)) {
                        missingEquivalent.add(brand)
                    }
                }
            }
        }

        // Extract eligibility clauses in tech spec
        val foundEligibility = mutableListOf<String>()
        if (documentType == DocumentType.TECHNICAL_SPEC) {
            eligibilityKeywordsInTechSpec.forEach { keyword ->
                if (content.contains(keyword, ignoreCase = true)) {
                    foundEligibility.add(keyword)
                }
            }
        }

        // Extract schedule items count
        val scheduleItemRegex = Regex("(?:Sıra\\s*No|Kalem\\s*No|İş\\s*Kalemi)\\s*[:.]?\\s*(\\d+)", RegexOption.IGNORE_CASE)
        val scheduleMatches = scheduleItemRegex.findAll(content).toList()
        val totalItems = if (scheduleMatches.isNotEmpty()) scheduleMatches.size else null

        return ExtractedParameters(
            warrantyMonths = warrantyMonths,
            deliveryDays = deliveryDays,
            penaltyRatePerDay = penaltyRate,
            mentionedBrands = mentioned,
            missingEquivalentBrands = missingEquivalent,
            eligibilityClausesInTechSpec = foundEligibility,
            totalScheduleItems = totalItems
        )
    }

    fun analyze(content: String, documentName: String, documentType: DocumentType): List<AuditFinding> {
        val findings = mutableListOf<AuditFinding>()
        val lines = content.lines()

        // 1. RULE-TECH-BRAND: 4734 Sayılı Kanun Madde 12 Marka / Model / Rekabet Kısıtlaması
        if (documentType == DocumentType.TECHNICAL_SPEC || documentType == DocumentType.GENERAL_DOCUMENT) {
            knownBrands.forEach { brand ->
                val brandRegex = Regex("\\b(?i)$brand\\b")
                lines.forEachIndexed { index, line ->
                    if (brandRegex.containsMatchIn(line)) {
                        val hasEquivalent = line.contains("veya dengi", ignoreCase = true) ||
                                line.contains("veya muadili", ignoreCase = true) ||
                                line.contains("veya eşdeğeri", ignoreCase = true)

                        if (!hasEquivalent) {
                            findings.add(
                                AuditFinding(
                                    id = UUID.randomUUID().toString(),
                                    ruleId = "RULE-TECH-BRAND-RESTRICTION",
                                    severity = Severity.CRITICAL,
                                    category = FindingCategory.BRAND_RISK,
                                    title = "Marka / Model Belirtilmiş Ancak \"Veya Dengi\" Eklenmemiş: \"$brand\"",
                                    description = "Teknik Şartnamede \"$brand\" markası/modeli açıkça belirtilmiş olup Kanun'un zorunlu kıldığı \"veya dengi\" / \"veya muadili\" ibaresine yer verilmemiştir. Bu durum ihalenin rekabeti engelleyici bulunarak iptal edilmesine (4734 Md. 12) yol açabilir.",
                                    detectedText = line.trim(),
                                    documentName = documentName,
                                    documentType = documentType,
                                    location = "Madde / Satır ${index + 1}",
                                    legislationRef = "4734 Sayılı Kamu İhale Kanunu Madde 12, Fıkra 2 & 3",
                                    kikPrecedentRef = "KİK Kararı: 2023/UM.I-1204 (Marka belirtilip veya dengi yazılmaması iptal gerekçesidir)",
                                    suggestion = "\"$brand\" ibaresinin yanına mutlaka \"... $brand veya dengi / muadili\" ifadesini ekleyiniz ya da markayı kaldırıp purely fonksiyonel teknik kriterleri tanımlayınız.",
                                    confidence = ConfidenceLevel.HIGH
                                )
                            )
                        } else {
                            findings.add(
                                AuditFinding(
                                    id = UUID.randomUUID().toString(),
                                    ruleId = "RULE-TECH-BRAND-EQUIVALENT",
                                    severity = Severity.WARNING,
                                    category = FindingCategory.BRAND_RISK,
                                    title = "Marka ve Dengi İfadesi Mevcut: \"$brand veya dengi\"",
                                    description = "\"$brand veya dengi\" ifadesi kullanılmıştır. Ancak 4734 Md. 12 uyarınca marka belirtilmesi yalnızca ulusal/uluslararası standartların bulunmaması veya teknik özelliklerin başka türlü tarif edilememesi hallerinde istisnaidir.",
                                    detectedText = line.trim(),
                                    documentName = documentName,
                                    documentType = documentType,
                                    location = "Madde / Satır ${index + 1}",
                                    legislationRef = "4734 Sayılı Kamu İhale Kanunu Madde 12",
                                    kikPrecedentRef = "KİK Kararı: 2024/UH.II-512 (Marka tarifinin teknik zorunluluk olup olmadığının idarece tevsiki gerekir)",
                                    suggestion = "Teknik özelliklerin standart parametrelerle (ISO/TSE normları, ölçüler, toleranslar) tarif edilmesinin mümkün olup olmadığını gözden geçiriniz.",
                                    confidence = ConfidenceLevel.MEDIUM
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. RULE-TECH-ELIGIBILITY: Teknik Şartnamede Yeterlik Kriteri
        if (documentType == DocumentType.TECHNICAL_SPEC) {
            eligibilityKeywordsInTechSpec.forEach { keyword ->
                lines.forEachIndexed { index, line ->
                    if (line.contains(keyword, ignoreCase = true)) {
                        findings.add(
                            AuditFinding(
                                id = UUID.randomUUID().toString(),
                                ruleId = "RULE-TECH-WRONGFUL-ELIGIBILITY",
                                severity = Severity.HIGH,
                                category = FindingCategory.ELIGIBILITY,
                                title = "Teknik Şartnamede Yeterlik Kriteri Düzenlemesi: \"$keyword\"",
                                description = "İsteklilerin yeterliğine ilişkin belgeler (iş deneyim belgesi, yetki belgesi, bilanço, kalite belgeleri vb.) Teknik Şartnamede değil, İdari Şartname'nin 7. maddesinde ve İhale İlanında düzenlenmelidir. Teknik şartnamede yer alan ancak idari şartnamede bulunmayan yeterlik belgeleri teklif değerlendirmesinde esas alınamaz.",
                                detectedText = line.trim(),
                                documentName = documentName,
                                documentType = documentType,
                                location = "Madde / Satır ${index + 1}",
                                legislationRef = "4734 Sayılı Kanun Madde 10 & Tip İdari Şartname Md. 7",
                                kikPrecedentRef = "KİK Genel Tebliği ve İstikrarlı Kurul Kararları (Teknik şartnameye yeterlik belgesi konulamaz)",
                                suggestion = "Bu kriteri Teknik Şartnameden çıkarıp İdari Şartname Madde 7 (Mesleki ve Teknik Yeterlik) bölümüne taşıyınız.",
                                confidence = ConfidenceLevel.HIGH
                            )
                        )
                    }
                }
            }
        }

        // 3. RULE-ADMIN-TEMINAT: İdari Şartname Geçici & Kesin Teminat Oranları
        if (documentType == DocumentType.ADMINISTRATIVE_SPEC) {
            val teminatRegex = Regex("geçici\\s*teminat[^\\n.]*%\\s*(\\d+[.,]?\\d*)", RegexOption.IGNORE_CASE)
            lines.forEachIndexed { index, line ->
                val match = teminatRegex.find(line)
                if (match != null) {
                    val rate = match.groupValues[1].replace(',', '.').toDoubleOrNull() ?: 0.0
                    if (rate < 3.0) {
                        findings.add(
                            AuditFinding(
                                id = UUID.randomUUID().toString(),
                                ruleId = "RULE-ADMIN-BID-BOND-LOW",
                                severity = Severity.CRITICAL,
                                category = FindingCategory.LEGISLATION,
                                title = "Geçici Teminat Oranı Kanuni Alt Sınırın Altında (%$rate)",
                                description = "4734 Sayılı Kanun'un 33. maddesi gereğince teklif edilen bedelin en az %3'ü oranında geçici teminat verilmesi zorunludur.",
                                detectedText = match.value,
                                documentName = documentName,
                                documentType = documentType,
                                location = "Madde / Satır ${index + 1}",
                                legislationRef = "4734 Sayılı Kanun Madde 33 (Geçici Teminat)",
                                suggestion = "Geçici teminat oranını en az \"%3'ünden az olmamak üzere\" olarak güncelleyiniz.",
                                confidence = ConfidenceLevel.HIGH
                            )
                        )
                    }
                }
            }
        }

        // 4. RULE-CONTRACT-PENALTY: Sözleşme Tasarısı Gecikme Cezası Oranı
        if (documentType == DocumentType.CONTRACT_DRAFT) {
            val penaltyRegex = Regex("gecikme\\s*cezası[^\\n.]*%\\s*(\\d+[.,]?\\d*)", RegexOption.IGNORE_CASE)
            lines.forEachIndexed { index, line ->
                val match = penaltyRegex.find(line)
                if (match != null) {
                    val rate = match.groupValues[1].replace(',', '.').toDoubleOrNull() ?: 0.0
                    if (rate > 1.0) {
                        findings.add(
                            AuditFinding(
                                id = UUID.randomUUID().toString(),
                                ruleId = "RULE-CONTRACT-EXCESSIVE-PENALTY",
                                severity = Severity.WARNING,
                                category = FindingCategory.LEGISLATION,
                                title = "Günlük Gecikme Cezası Oranı Yüksek Görünüyor (%$rate)",
                                description = "Tip Sözleşmelerde günlük gecikme cezası oranı genellikle sözleşme bedelinin onbinde 5'i ( %0.05 ) ile binde 2'si ( %0.2 ) arasında belirlenir. Yüksek oranlar rekabeti daraltabilir ve itiraz konusu olabilir.",
                                detectedText = match.value,
                                documentName = documentName,
                                documentType = documentType,
                                location = "Madde / Satır ${index + 1}",
                                legislationRef = "4735 Sayılı Kamu İhale Sözleşmeleri Kanunu ve Tip Sözleşmeler",
                                suggestion = "Gecikme cezası oranının ilgili alım türünün Tip Sözleşmesine uygunluğunu teyit ediniz.",
                                confidence = ConfidenceLevel.MEDIUM
                            )
                        )
                    }
                }
            }
        }

        return findings
    }
}
