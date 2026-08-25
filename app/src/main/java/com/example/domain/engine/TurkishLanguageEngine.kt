package com.example.domain.engine

import com.example.domain.model.AuditFinding
import com.example.domain.model.ConfidenceLevel
import com.example.domain.model.DocumentType
import com.example.domain.model.FindingCategory
import com.example.domain.model.Severity
import java.util.UUID

object TurkishLanguageEngine {

    private val commonSpellingMistakes = mapOf(
        "herkez" to "herkes",
        "yanlız" to "yalnız",
        "yanlışlık la" to "yanlışlıkla",
        "orjinal" to "orijinal",
        "labaratuar" to "laboratuvar",
        "insiyatif" to "inisiyatif",
        "kılavuz" to "kılavuz",
        "klavuz" to "kılavuz",
        "döküman" to "doküman",
        "mütahit" to "müteahhit",
        "mütaahit" to "müteahhit",
        "şartnameye" to "şartnameye",
        "tasarrı" to "tasarı",
        "hakediş" to "hakediş",
        "isteklinin" to "isteklinin",
        "teminatın" to "teminatın",
        "muayene kabul" to "muayene ve kabul",
        "teslimat" to "teslim",
        "mataryal" to "materyal",
        "karekter" to "karakter",
        "prosedür" to "prosedür",
        "taslak" to "taslak"
    )

    fun analyze(content: String, documentName: String, documentType: DocumentType): List<AuditFinding> {
        val findings = mutableListOf<AuditFinding>()
        val lines = content.lines()

        // 1. Common spelling & terminology checks
        commonSpellingMistakes.forEach { (wrong, correct) ->
            val regex = Regex("\\b(?i)$wrong\\b")
            lines.forEachIndexed { index, line ->
                val match = regex.find(line)
                if (match != null) {
                    findings.add(
                        AuditFinding(
                            id = UUID.randomUUID().toString(),
                            ruleId = "RULE-LANG-SPELL",
                            severity = Severity.WARNING,
                            category = FindingCategory.TURKISH_LANGUAGE,
                            title = "Resmi Yazım ve Terminoloji Hatası: \"$wrong\"",
                            description = "Kamu yazışma ve ihale terminolojisinde \"$wrong\" yerine TDK ve kamu standardı olan \"$correct\" ifadesi kullanılmalıdır.",
                            detectedText = match.value,
                            documentName = documentName,
                            documentType = documentType,
                            location = "Satır ${index + 1}",
                            legislationRef = "Resmî Yazışmalarda Uygulanacak Usul ve Esaslar Hakkında Yönetmelik",
                            suggestion = "\"$wrong\" ifadesini \"$correct\" olarak düzeltiniz.",
                            confidence = ConfidenceLevel.HIGH
                        )
                    )
                }
            }
        }

        // 2. Sayı ve Yazı ile Uyumsuzluk / Standart Yazım Kontrolü (örn: "10 (on)", "5 (beş)")
        val numberWordRegex = Regex("(\\d+)\\s*\\(([^)]+)\\)")
        lines.forEachIndexed { index, line ->
            numberWordRegex.findAll(line).forEach { match ->
                val numStr = match.groupValues[1]
                val wordStr = match.groupValues[2].trim().lowercase()
                val expectedWord = numberToTurkishWord(numStr.toLongOrNull() ?: -1)
                if (expectedWord != null && !wordStr.contains(expectedWord)) {
                    findings.add(
                        AuditFinding(
                            id = UUID.randomUUID().toString(),
                            ruleId = "RULE-LANG-NUMWORD",
                            severity = Severity.CRITICAL,
                            category = FindingCategory.TURKISH_LANGUAGE,
                            title = "Rakam ve Yazıyla Sayı Uyuşmazlığı: \"${match.value}\"",
                            description = "Parantez içi yazıyla belirtilen değer ($wordStr) ile rakamsal değer ($numStr -> $expectedWord) birbiriyle uyuşmamaktadır. İhale uyuşmazlıklarında KİK ve yargı kararları yazıyla olan ifadeyi veya çelişkiyi esas alabilir.",
                            detectedText = match.value,
                            documentName = documentName,
                            documentType = documentType,
                            location = "Satır ${index + 1}",
                            legislationRef = "4734 Sayılı Kanun ve Tip Şartname Genel Hükümleri",
                            suggestion = "\"${match.value}\" ifadesini \"$numStr ($expectedWord)\" olarak eşitleyiniz.",
                            confidence = ConfidenceLevel.HIGH
                        )
                    )
                }
            }
        }

        // 3. Tarih Formatı Kontrolü (DD.MM.YYYY standardı)
        val irregularDateRegex = Regex("\\b(\\d{1,2})[/\\-](\\d{1,2})[/\\-](\\d{4})\\b")
        lines.forEachIndexed { index, line ->
            irregularDateRegex.findAll(line).forEach { match ->
                val day = match.groupValues[1].padStart(2, '0')
                val month = match.groupValues[2].padStart(2, '0')
                val year = match.groupValues[3]
                findings.add(
                    AuditFinding(
                        id = UUID.randomUUID().toString(),
                        ruleId = "RULE-LANG-DATE",
                        severity = Severity.SUGGESTION,
                        category = FindingCategory.FORMAT_STRUCTURE,
                        title = "Standart Dışı Tarih Formatı: \"${match.value}\"",
                        description = "Resmi yazışmalarda tarihler nokta (.) ayracı ile GG.AA.YYYY formatında yazılmalıdır.",
                        detectedText = match.value,
                        documentName = documentName,
                        documentType = documentType,
                        location = "Satır ${index + 1}",
                        legislationRef = "Resmî Yazışma Yönetmeliği Madde 14",
                        suggestion = "\"${match.value}\" yerine \"$day.$month.$year\" standardını kullanınız.",
                        confidence = ConfidenceLevel.HIGH
                    )
                )
            }
        }

        // 4. Bağlaç olan da/de'nin bitişik yazımı veya ek olan -de'nin ayrı yazımı kontrolü
        val suspiciousDaRegex = Regex("\\b(idare de|taraflar da|şartname de|kanun da)\\b", RegexOption.IGNORE_CASE)
        lines.forEachIndexed { index, line ->
            suspiciousDaRegex.findAll(line).forEach { match ->
                val text = match.value
                val correction = when (text.lowercase()) {
                    "şartname de" -> "şartnamede (bulunma hali ise)"
                    "kanun da" -> "kanunda (bulunma hali ise)"
                    else -> "$text (bağlam kontrolü)"
                }
                findings.add(
                    AuditFinding(
                        id = UUID.randomUUID().toString(),
                        ruleId = "RULE-LANG-DADE",
                        severity = Severity.SUGGESTION,
                        category = FindingCategory.TURKISH_LANGUAGE,
                        title = "Muhtemel Bağlaç / Ek Ayrımı Hatası: \"$text\"",
                        description = "Bulunma durumu eki olan \"-de/-da\" bitişik, bağlaç olan \"de/da\" ayrı yazılır.",
                        detectedText = text,
                        documentName = documentName,
                        documentType = documentType,
                        location = "Satır ${index + 1}",
                        legislationRef = "TDK İmla Kuralları",
                        suggestion = "Cümlenin anlamına göre doğruluğunu kontrol ediniz: $correction",
                        confidence = ConfidenceLevel.MEDIUM
                    )
                )
            }
        }

        return findings
    }

    private fun numberToTurkishWord(number: Long): String? {
        if (number < 0 || number > 999999) return null
        if (number == 0L) return "sıfır"

        val ones = arrayOf("", "bir", "iki", "üç", "dört", "beş", "altı", "yedi", "sekiz", "dokuz")
        val tens = arrayOf("", "on", "yirmi", "otuz", "kırk", "elli", "altmış", "yetmiş", "seksen", "doksan")

        val n = number.toInt()
        val result = StringBuilder()

        val thousands = n / 1000
        val remainder = n % 1000

        if (thousands > 0) {
            if (thousands == 1) {
                result.append("bin ")
            } else {
                val h = thousands / 100
                val t = (thousands % 100) / 10
                val o = thousands % 10
                if (h > 0) {
                    if (h > 1) result.append(ones[h]).append(" ")
                    result.append("yüz ")
                }
                if (t > 0) result.append(tens[t]).append(" ")
                if (o > 0) result.append(ones[o]).append(" ")
                result.append("bin ")
            }
        }

        if (remainder > 0) {
            val h = remainder / 100
            val t = (remainder % 100) / 10
            val o = remainder % 10
            if (h > 0) {
                if (h > 1) result.append(ones[h]).append(" ")
                result.append("yüz ")
            }
            if (t > 0) result.append(tens[t]).append(" ")
            if (o > 0) result.append(ones[o]).append(" ")
        }

        return result.toString().trim()
    }
}
