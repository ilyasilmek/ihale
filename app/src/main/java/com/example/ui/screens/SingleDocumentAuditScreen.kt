package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DocumentType
import com.example.domain.model.Severity
import com.example.ui.components.FindingCard
import com.example.ui.components.ScoreCard
import com.example.ui.theme.BrandBlue700
import com.example.ui.theme.Navy900
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeveritySuggestion
import com.example.ui.theme.SeverityWarning
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleDocumentAuditScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val docName by viewModel.singleDocName.collectAsState()
    val docType by viewModel.singleDocType.collectAsState()
    val docContent by viewModel.singleDocContent.collectAsState()
    val findings by viewModel.singleDocFindings.collectAsState()
    val score by viewModel.singleDocScore.collectAsState()
    val sensitiveData by viewModel.singleDocSensitiveData.collectAsState()
    val activeSeverityFilter by viewModel.activeSeverityFilter.collectAsState()

    var typeMenuExpanded by remember { mutableStateOf(false) }

    val filteredFindings = if (activeSeverityFilter != null) {
        findings.filter { it.severity == activeSeverityFilter }
    } else {
        findings
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier.fillMaxSize().testTag("single_doc_screen")
    ) {
        // Top Nav Bar
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Tek Belge Denetimi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Yazım, Mevzuat, Biçim ve KVKK Analizi",
                        fontSize = 12.sp,
                        color = Slate600
                    )
                }
            }
        }

        // Document Meta & Quick Action Bar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Slate200, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Document Type Dropdown
                        ExposedDropdownMenuBox(
                            expanded = typeMenuExpanded,
                            onExpandedChange = { typeMenuExpanded = !typeMenuExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = docType.title,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Belge Türü", fontSize = 11.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = typeMenuExpanded,
                                onDismissRequest = { typeMenuExpanded = false }
                            ) {
                                listOf(
                                    DocumentType.TECHNICAL_SPEC,
                                    DocumentType.ADMINISTRATIVE_SPEC,
                                    DocumentType.CONTRACT_DRAFT,
                                    DocumentType.PRICE_SCHEDULE,
                                    DocumentType.GENERAL_DOCUMENT
                                ).forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.title) },
                                        onClick = {
                                            viewModel.setSingleDocContent(docContent, docName, type)
                                            typeMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Quick Sample Text Button
                        Button(
                            onClick = { viewModel.loadSampleIntoSingleDoc() },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_load_single_sample")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Örnek Metin", fontSize = 12.sp)
                        }
                    }

                    // Content Input Area
                    OutlinedTextField(
                        value = docContent,
                        onValueChange = { viewModel.setSingleDocContent(it) },
                        label = { Text("Belge Metnini Buraya Yapıştırın veya Yazın") },
                        placeholder = { Text("Teknik şartname maddelerini, resmi yazışma metnini veya ihale ilanını buraya yapıştırınız...") },
                        minLines = 6,
                        maxLines = 14,
                        modifier = Modifier.fillMaxWidth().testTag("input_single_doc_content")
                    )

                    // Text statistics
                    val wordCount = if (docContent.isBlank()) 0 else docContent.trim().split(Regex("\\s+")).size
                    val charCount = docContent.length
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📊 $wordCount Kelime • $charCount Karakter",
                            fontSize = 11.sp,
                            color = Slate600
                        )
                        if (docContent.isNotEmpty()) {
                            TextButton(
                                onClick = { viewModel.setSingleDocContent("") }
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Metni Temizle", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // KVKK Redaction Alert Card (if sensitive data detected)
        if (sensitiveData.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.5.dp, SeverityCritical.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = SeverityCritical,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "HASSAS VERİ / KVKK ALARMI (${sensitiveData.size} Öğe)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SeverityCritical
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Dokümanda T.C. Kimlik Numarası, IBAN veya Telefon tespit edildi. Kamuya açık şartnamelerde bu verilerin gizlenmesi zorunludur.",
                            fontSize = 12.sp,
                            color = Navy900
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.applySingleDocRedaction() },
                            colors = ButtonDefaults.buttonColors(containerColor = SeverityCritical),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("btn_apply_redaction")
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tüm Hassas Verileri Güvenle Maskele (Redact)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Score Card
        if (score != null) {
            item {
                ScoreCard(score = score!!)
            }
        }

        // Findings Filter Bar
        if (findings.isNotEmpty()) {
            item {
                Column {
                    Text(
                        text = "DENETİM BULGULARI (${findings.size} Tespit)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = activeSeverityFilter == null,
                                onClick = { viewModel.setSeverityFilter(null) },
                                label = { Text("Tümü (${findings.size})", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors()
                            )
                        }
                        item {
                            val count = findings.count { it.severity == Severity.CRITICAL }
                            FilterChip(
                                selected = activeSeverityFilter == Severity.CRITICAL,
                                onClick = {
                                    viewModel.setSeverityFilter(if (activeSeverityFilter == Severity.CRITICAL) null else Severity.CRITICAL)
                                },
                                label = { Text("🔴 Kritik ($count)", fontSize = 11.sp) }
                            )
                        }
                        item {
                            val count = findings.count { it.severity == Severity.HIGH }
                            FilterChip(
                                selected = activeSeverityFilter == Severity.HIGH,
                                onClick = {
                                    viewModel.setSeverityFilter(if (activeSeverityFilter == Severity.HIGH) null else Severity.HIGH)
                                },
                                label = { Text("🟠 Yüksek ($count)", fontSize = 11.sp) }
                            )
                        }
                        item {
                            val count = findings.count { it.severity == Severity.WARNING }
                            FilterChip(
                                selected = activeSeverityFilter == Severity.WARNING,
                                onClick = {
                                    viewModel.setSeverityFilter(if (activeSeverityFilter == Severity.WARNING) null else Severity.WARNING)
                                },
                                label = { Text("🟡 Dikkat ($count)", fontSize = 11.sp) }
                            )
                        }
                        item {
                            val count = findings.count { it.severity == Severity.SUGGESTION }
                            FilterChip(
                                selected = activeSeverityFilter == Severity.SUGGESTION,
                                onClick = {
                                    viewModel.setSeverityFilter(if (activeSeverityFilter == Severity.SUGGESTION) null else Severity.SUGGESTION)
                                },
                                label = { Text("🔵 Öneri ($count)", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            items(filteredFindings) { finding ->
                FindingCard(finding = finding)
            }
        } else if (docContent.isNotBlank()) {
            item {
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text("🟢", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Herhangi Bir Hata veya Risk Tespit Edilmedi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                        Text(
                            text = "Metninizde standart dışı marka kısıtlaması, imla hatası veya KVKK riski bulunmamaktadır.",
                            fontSize = 12.sp,
                            color = Color(0xFF166534),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
