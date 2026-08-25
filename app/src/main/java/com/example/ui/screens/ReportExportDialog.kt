package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TenderDocumentEntity
import com.example.data.local.entity.TenderProjectEntity
import com.example.domain.model.AuditFinding
import com.example.domain.model.AuditScore
import com.example.domain.model.Severity
import com.example.ui.theme.BrandBlue700
import com.example.ui.theme.Navy900
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportExportDialog(
    project: TenderProjectEntity,
    documents: List<TenderDocumentEntity>,
    findings: List<AuditFinding>,
    score: AuditScore,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    val currentDate = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr")).format(Date())

    val criticals = findings.filter { it.severity == Severity.CRITICAL }
    val highs = findings.filter { it.severity == Severity.HIGH }
    val warnings = findings.filter { it.severity == Severity.WARNING }

    val formattedReportText = buildString {
        appendLine("=================================================================")
        appendLine("T.C. KAMU İHALE DOKÜMANI ÖN KONTROL VE UYGUNLUK RAPORU")
        appendLine("Rapor Tarihi: $currentDate")
        appendLine("=================================================================")
        appendLine("1. İHALE KİMLİK BİLGİLERİ")
        appendLine("• İhale Kayıt Numarası (İKN): ${project.tenderNumber}")
        appendLine("• İhale Adı / Konusu: ${project.title}")
        appendLine("• İdare / Kurum Adı: ${project.institutionName}")
        appendLine("• İhale Türü: ${project.tenderType.displayName} | Usul: ${project.tenderProcedure.displayName}")
        appendLine("• İncelenen Doküman Sayısı: ${documents.size}")
        appendLine()
        appendLine("2. RİSK VE UYGUNLUK SKORLARI")
        appendLine("• Genel İhale Risk Skoru: ${score.overallScore} / 100")
        appendLine("  - Mevzuat Uyumu: ${score.legislationScore}/100")
        appendLine("  - Çapraz Doküman Tutarlılığı: ${score.crossConsistencyScore}/100")
        appendLine("  - Marka/Model ve Rekabet Riski: ${score.brandRiskScore}/100")
        appendLine("  - Yeterlik Kriterleri: ${score.eligibilityScore}/100")
        appendLine("  - EKAP Süreç Uyumu: ${score.ekapScore}/100")
        appendLine("  - Türkçe Yazım & Biçim: ${score.languageScore}/100")
        appendLine()
        appendLine("3. TESPİT VE BULGU ÖZETİ")
        appendLine("• Toplam Tespit: ${findings.size}")
        appendLine("• [🔴 KRİTİK]: ${criticals.size} adet (İhale İptali veya Teklif Geçersizliği Riski)")
        appendLine("• [🟠 YÜKSEK]: ${highs.size} adet (Mevzuat Uyuşmazlığı / İtiraz Riski)")
        appendLine("• [🟡 DİKKAT]: ${warnings.size} adet (Düzenleme / İyileştirme Gerekir)")
        appendLine()
        appendLine("4. ÖNEMLİ VE KRİTİK BULGULAR DETAYI")
        if (criticals.isEmpty() && highs.isEmpty()) {
            appendLine("• Kritik veya yüksek düzeyde mevzuat aykırılığı tespit edilmemiştir.")
        } else {
            criticals.forEachIndexed { i, f ->
                appendLine("[KRİTİK-${i + 1}] ${f.title}")
                appendLine("  - Belge/Yer: ${f.documentName} (${f.location})")
                appendLine("  - Açıklama: ${f.description}")
                if (f.legislationRef != null) appendLine("  - Mevzuat Dayanağı: ${f.legislationRef}")
                if (f.kikPrecedentRef != null) appendLine("  - KİK Emsali: ${f.kikPrecedentRef}")
                appendLine("  - Çözüm Önerisi: ${f.suggestion}")
                appendLine()
            }
            highs.forEachIndexed { i, f ->
                appendLine("[YÜKSEK-${i + 1}] ${f.title}")
                appendLine("  - Belge/Yer: ${f.documentName} (${f.location})")
                appendLine("  - Açıklama: ${f.description}")
                if (f.legislationRef != null) appendLine("  - Mevzuat Dayanağı: ${f.legislationRef}")
                appendLine("  - Çözüm Önerisi: ${f.suggestion}")
                appendLine()
            }
        }
        appendLine("=================================================================")
        appendLine("YASAL UYARI: Bu rapor, Akıllı Kamu İhale ve Belge Denetim Platformu")
        appendLine("tarafından otomatik olarak üretilmiş olup resmi hukuki danışmanlık")
        appendLine("yerine geçmez. Nihai kararlar yetkili komisyona aittir.")
        appendLine("=================================================================")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = BrandBlue700,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "İhale Ön Kontrol Raporu",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Rapor Tarihi: $currentDate • Skor: ${score.overallScore}/100",
                    fontSize = 12.sp,
                    color = Slate600
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = Slate100,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .border(1.dp, Slate200, RoundedCornerShape(8.dp))
                ) {
                    LazyColumn(modifier = Modifier.padding(12.dp)) {
                        item {
                            Text(
                                text = formattedReportText,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Navy900,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("İhale Denetim Raporu", formattedReportText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Rapor metni panoya kopyalandı", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("btn_copy_report_text")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kopyala", fontSize = 12.sp)
                }

                Button(
                    onClick = onSave,
                    modifier = Modifier.testTag("btn_save_report_history")
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Geçmişe Kaydet", fontSize = 12.sp)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Kapat") }
        }
    )
}
