package com.example

import com.example.domain.engine.CrossDocumentAuditEngine
import com.example.domain.engine.DocumentWithParams
import com.example.domain.engine.ExtractedParameters
import com.example.domain.engine.LegislationRuleEngine
import com.example.domain.engine.SensitiveDataRedactionEngine
import com.example.domain.engine.TurkishLanguageEngine
import com.example.domain.model.DocumentType
import com.example.domain.model.FindingCategory
import com.example.domain.model.SensitiveDataType
import com.example.domain.model.Severity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testTurkishNumberWordDiscrepancy() {
        val content = "Yüklenici tarafından 10 (oniki) adet sinyal modülü teslim edilecektir."
        val findings = TurkishLanguageEngine.analyze(content, "Teknik Şartname", DocumentType.TECHNICAL_SPEC)

        val numberFinding = findings.firstOrNull { it.ruleId == "RULE-LANG-NUMBER-WORD-MISMATCH" }
        assertTrue("Rakam ile yazı uyuşmazlığı tespit edilmelidir", numberFinding != null)
        assertEquals(Severity.HIGH, numberFinding?.severity)
    }

    @Test
    fun testTurkishSpellingCorrection() {
        val content = "Malzemeler için orjinal labaratuar test raporu sunulmalıdır."
        val findings = TurkishLanguageEngine.analyze(content, "Şartname", DocumentType.TECHNICAL_SPEC)

        val spellingFindings = findings.filter { it.ruleId.startsWith("RULE-LANG-SPELL") }
        assertTrue("İmla hataları (orjinal, labaratuar) tespit edilmelidir", spellingFindings.size >= 2)
    }

    @Test
    fun testBrandRestrictionWithoutEquivalent() {
        val content = "Sistem kontrol ünitesi Siemens S7 PLC ile uyumlu olacaktır."
        val findings = LegislationRuleEngine.analyze(content, "Teknik Şartname", DocumentType.TECHNICAL_SPEC)

        val brandFinding = findings.firstOrNull { it.ruleId == "RULE-TECH-BRAND-RESTRICTION" }
        assertTrue("Marka yazılıp 'veya dengi' yazılmadığında KRİTİK risk üretilmelidir", brandFinding != null)
        assertEquals(Severity.CRITICAL, brandFinding?.severity)
    }

    @Test
    fun testBrandRestrictionWithEquivalent() {
        val content = "Sistem kontrol ünitesi Siemens veya dengi PLC ile uyumlu olacaktır."
        val findings = LegislationRuleEngine.analyze(content, "Teknik Şartname", DocumentType.TECHNICAL_SPEC)

        val brandWarning = findings.firstOrNull { it.ruleId == "RULE-TECH-BRAND-EQUIVALENT" }
        assertTrue("Marka veya dengi yazıldığında UYARI üretilmelidir", brandWarning != null)
        assertEquals(Severity.WARNING, brandWarning?.severity)
    }

    @Test
    fun testWrongfulEligibilityInTechSpec() {
        val content = "İstekliler iş deneyim belgesi ve ISO 9001 belgesini teklif aşamasında sunmalıdır."
        val findings = LegislationRuleEngine.analyze(content, "Teknik Şartname", DocumentType.TECHNICAL_SPEC)

        val eligFinding = findings.firstOrNull { it.ruleId == "RULE-TECH-WRONGFUL-ELIGIBILITY" }
        assertTrue("Teknik şartnamede yeterlik belgesi istendiğinde tespit edilmelidir", eligFinding != null)
        assertEquals(FindingCategory.ELIGIBILITY, eligFinding?.category)
    }

    @Test
    fun testCrossDocumentWarrantyMismatch() {
        val techDoc = DocumentWithParams(
            id = "1",
            name = "Teknik Şartname",
            type = DocumentType.TECHNICAL_SPEC,
            content = "Garanti 24 ay sürelidir.",
            params = ExtractedParameters(warrantyMonths = 24)
        )
        val adminDoc = DocumentWithParams(
            id = "2",
            name = "İdari Şartname",
            type = DocumentType.ADMINISTRATIVE_SPEC,
            content = "Garanti 12 ay sürelidir.",
            params = ExtractedParameters(warrantyMonths = 12)
        )

        val findings = CrossDocumentAuditEngine.performCrossAudit(listOf(techDoc, adminDoc))
        val mismatchFinding = findings.firstOrNull { it.ruleId == "RULE-CROSS-WARRANTY-MISMATCH" }

        assertTrue("24 ay vs 12 ay garanti çelişkisi tespit edilmelidir", mismatchFinding != null)
        assertEquals(Severity.CRITICAL, mismatchFinding?.severity)
    }

    @Test
    fun testSensitiveDataRedaction() {
        val content = "Komisyon Başkanı Ahmet Yılmaz T.C. Kimlik: 10000000146, İletişim: 0532 555 12 34"
        val sensitiveItems = SensitiveDataRedactionEngine.findSensitiveData(content)

        assertTrue("Hassas veriler bulunmalıdır", sensitiveItems.isNotEmpty())

        val redacted = SensitiveDataRedactionEngine.applyRedaction(content, sensitiveItems)
        assertFalse("Orijinal telefon açıkta kalmamalıdır", redacted.contains("0532 555 12 34"))
    }
}
