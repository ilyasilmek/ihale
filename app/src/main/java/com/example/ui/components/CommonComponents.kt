package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AuditFinding
import com.example.domain.model.AuditScore
import com.example.domain.model.Severity
import com.example.ui.theme.BrandBlue200
import com.example.ui.theme.BrandBlue700
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeveritySuccess
import com.example.ui.theme.SeveritySuggestion
import com.example.ui.theme.SeverityWarning
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600

@Composable
fun SeverityBadge(severity: Severity, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (severity) {
        Severity.CRITICAL -> Pair(SeverityCritical.copy(alpha = 0.15f), SeverityCritical)
        Severity.HIGH -> Pair(SeverityHigh.copy(alpha = 0.15f), SeverityHigh)
        Severity.WARNING -> Pair(SeverityWarning.copy(alpha = 0.15f), SeverityWarning)
        Severity.SUGGESTION -> Pair(SeveritySuggestion.copy(alpha = 0.15f), SeveritySuggestion)
        Severity.SUCCESS -> Pair(SeveritySuccess.copy(alpha = 0.15f), SeveritySuccess)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${severity.badge} ${severity.label}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun FindingCard(
    finding: AuditFinding,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var showWhyDialog by remember { mutableStateOf(false) }

    val borderColor = when (finding.severity) {
        Severity.CRITICAL -> SeverityCritical.copy(alpha = 0.6f)
        Severity.HIGH -> SeverityHigh.copy(alpha = 0.5f)
        Severity.WARNING -> SeverityWarning.copy(alpha = 0.5f)
        Severity.SUGGESTION -> SeveritySuggestion.copy(alpha = 0.4f)
        Severity.SUCCESS -> SeveritySuccess.copy(alpha = 0.4f)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .testTag("finding_card_${finding.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Severity Badge + Category + Expand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SeverityBadge(severity = finding.severity)
                Text(
                    text = finding.category.displayName,
                    fontSize = 12.sp,
                    color = Slate600,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = finding.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Location + Document Tag
            Text(
                text = "📍 ${finding.documentName} • ${finding.location}",
                fontSize = 12.sp,
                color = Slate600
            )

            // Detected snippet preview
            if (finding.detectedText.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Slate100,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "\"${finding.detectedText}\"",
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Navy900,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Expandable details
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = Slate200)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Sorun ve Risk Açıklaması:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = finding.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    if (finding.legislationRef != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = BrandBlue700,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dayanak: ${finding.legislationRef}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandBlue700
                            )
                        }
                    }

                    if (finding.kikPrecedentRef != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚖️ Emsal: ${finding.kikPrecedentRef}",
                            fontSize = 11.sp,
                            color = Slate600
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = BrandBlue200.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "💡 Önerilen Düzeltme:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandBlue700
                            )
                            Text(
                                text = finding.suggestion,
                                fontSize = 13.sp,
                                color = Navy900,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { showWhyDialog = true },
                            modifier = Modifier.testTag("btn_why_${finding.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("NEDEN? (Mevzuat Dayanağı)", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Bottom toggle hint
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Daralt" else "Genişlet",
                    tint = Slate600,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    // "NEDEN?" Explanation Dialog
    if (showWhyDialog) {
        AlertDialog(
            onDismissRequest = { showWhyDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = BrandBlue700,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Bu Kontrol Neden Yapılıyor?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = finding.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BrandBlue700
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📜 İlgili Mevzuat:\n${finding.legislationRef ?: "Kamu İhale Mevzuatı ve Tip Şartnameler"}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Navy900
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = finding.description,
                        fontSize = 13.sp,
                        color = Slate600
                    )
                    if (finding.kikPrecedentRef != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "📌 KİK Uygulaması:\n${finding.kikPrecedentRef}",
                            fontSize = 12.sp,
                            color = Slate600
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "⚠️ Bu sistem otomatik ön kontrol aracıdır. Nihai hukuki değerlendirme yetkili idare ve komisyonca yapılmalıdır.",
                        fontSize = 11.sp,
                        color = Slate600
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showWhyDialog = false }) {
                    Text("Anladım")
                }
            }
        )
    }
}

@Composable
fun ScoreCard(
    score: AuditScore,
    modifier: Modifier = Modifier
) {
    val overall = score.overallScore
    val (scoreColor, scoreLabel) = when {
        overall >= 85 -> Pair(SeveritySuccess, "Düşük Risk / Genel Olarak Uygun")
        overall >= 70 -> Pair(SeverityWarning, "Orta Risk / İnceleme Önerilir")
        overall >= 50 -> Pair(SeverityHigh, "Yüksek Risk / Ciddi Çelişkiler Var")
        else -> Pair(SeverityCritical, "Kritik Risk / İhale İptal Riski Yüksek")
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "İHALE DOKÜMANI RİSK SKORU",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Slate600
                    )
                    Text(
                        text = scoreLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = scoreColor,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Circular Score Badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(scoreColor.copy(alpha = 0.12f))
                        .border(2.5.dp, scoreColor, CircleShape)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$overall",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = scoreColor
                        )
                        Text(
                            text = "/100",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate600
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Slate200)
            Spacer(modifier = Modifier.height(12.dp))

            // Sub-scores grid
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SubScoreItem("Mevzuat", score.legislationScore)
                SubScoreItem("Çapraz Uyum", score.crossConsistencyScore)
                SubScoreItem("Rekabet/Marka", score.brandRiskScore)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SubScoreItem("Yeterlik", score.eligibilityScore)
                SubScoreItem("EKAP Uyumu", score.ekapScore)
                SubScoreItem("Dil & Biçim", score.languageScore)
            }
        }
    }
}

@Composable
fun SubScoreItem(label: String, scoreValue: Int) {
    val color = when {
        scoreValue >= 85 -> SeveritySuccess
        scoreValue >= 70 -> SeverityWarning
        scoreValue >= 50 -> SeverityHigh
        else -> SeverityCritical
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Text(text = label, fontSize = 11.sp, color = Slate600)
        Text(
            text = "$scoreValue/100",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun DisclaimerBanner(modifier: Modifier = Modifier) {
    Surface(
        color = Slate100,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = Slate600,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Yasal Bildirim: Bu platform ihale hazırlık ve belge denetim süreçleri için otomatik bir ön kontrol ve analiz aracıdır. Hukuki müşavirlik veya resmi KİK görüşü yerine geçmez. Nihai kararlar yetkili komisyon ve idareye aittir.",
                fontSize = 11.sp,
                color = Slate600,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun DocumentRelationshipFlow(
    hasNotice: Boolean,
    hasAdminSpec: Boolean,
    hasTechSpec: Boolean,
    hasPriceSchedule: Boolean,
    hasContract: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "İHALE DOSYASI İLİŞKİ GRAFİĞİ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Slate600
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                DocNode("İlan", hasNotice)
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate600, modifier = Modifier.size(16.dp))
                DocNode("İdari Şartname", hasAdminSpec)
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate600, modifier = Modifier.size(16.dp))
                DocNode("Teknik Şart.", hasTechSpec)
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate600, modifier = Modifier.size(16.dp))
                DocNode("Teklif Cetveli", hasPriceSchedule)
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate600, modifier = Modifier.size(16.dp))
                DocNode("Sözleşme", hasContract)
            }
        }
    }
}

@Composable
fun DocNode(label: String, isLoaded: Boolean) {
    val bgColor = if (isLoaded) BrandBlue700 else Slate100
    val textColor = if (isLoaded) Color.White else Slate600

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isLoaded) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
        )
    }
}
