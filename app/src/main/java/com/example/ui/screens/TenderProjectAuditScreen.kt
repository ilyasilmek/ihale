package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
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
import com.example.data.local.entity.TenderDocumentEntity
import com.example.domain.model.DocumentType
import com.example.domain.model.FindingCategory
import com.example.domain.model.Severity
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.DocumentRelationshipFlow
import com.example.ui.components.FindingCard
import com.example.ui.components.ScoreCard
import com.example.ui.theme.BrandBlue700
import com.example.ui.theme.Navy900
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeverityHigh
import com.example.ui.theme.SeveritySuccess
import com.example.ui.theme.SeveritySuggestion
import com.example.ui.theme.SeverityWarning
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenderProjectAuditScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val auditResult by viewModel.projectAuditResult.collectAsState()
    val activeSeverityFilter by viewModel.activeSeverityFilter.collectAsState()
    val activeCategoryFilter by viewModel.activeCategoryFilter.collectAsState()

    var showAddDocDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var selectedDocToView by remember { mutableStateOf<TenderDocumentEntity?>(null) }

    if (auditResult == null) {
        Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("İhale dosyası yükleniyor...")
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                    Text("Ana Sayfaya Dön")
                }
            }
        }
        return
    }

    val result = auditResult!!
    val project = result.project
    val documents = result.documents
    val allFindings = result.findings
    val score = result.score

    val filteredFindings = allFindings.filter { finding ->
        val matchSeverity = activeSeverityFilter == null || finding.severity == activeSeverityFilter
        val matchCategory = activeCategoryFilter == null || finding.category == activeCategoryFilter
        matchSeverity && matchCategory
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier.fillMaxSize().testTag("tender_project_screen")
    ) {
        // 1. Top Header Bar
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "İKN: ${project.tenderNumber}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue700
                        )
                        Text(
                            text = project.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Button(
                    onClick = { showReportDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_export_report")
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rapor Al", fontSize = 12.sp)
                }
            }
        }

        // 2. Project Metadata Card
        item {
            Surface(
                color = Slate100,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Column {
                        Text("İdare: ${project.institutionName}", fontSize = 12.sp, color = Navy900, fontWeight = FontWeight.SemiBold)
                        Text("İhale Türü: ${project.tenderType.displayName}", fontSize = 11.sp, color = Slate600)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Usul: ${project.tenderProcedure.displayName}", fontSize = 11.sp, color = Slate600)
                        Text("Belge Sayısı: ${documents.size} Doküman", fontSize = 11.sp, color = BrandBlue700, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. Document Relationship Flow
        item {
            DocumentRelationshipFlow(
                hasNotice = documents.any { it.documentType == DocumentType.TENDER_NOTICE },
                hasAdminSpec = documents.any { it.documentType == DocumentType.ADMINISTRATIVE_SPEC },
                hasTechSpec = documents.any { it.documentType == DocumentType.TECHNICAL_SPEC },
                hasPriceSchedule = documents.any { it.documentType == DocumentType.PRICE_SCHEDULE },
                hasContract = documents.any { it.documentType == DocumentType.CONTRACT_DRAFT }
            )
        }

        // 4. Documents in Project Section
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "İHALE DOKÜMANLARI (${documents.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate600
                )
                TextButton(
                    onClick = { showAddDocDialog = true },
                    modifier = Modifier.testTag("btn_add_document_to_project")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Doküman Ekle", fontSize = 12.sp)
                }
            }
        }

        items(documents) { doc ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate200, RoundedCornerShape(10.dp))
                    .clickable { selectedDocToView = doc }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Surface(
                        color = BrandBlue700.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = doc.documentType.shortCode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue700,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = doc.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${doc.documentType.title} • ${doc.wordCount} Kelime",
                            fontSize = 11.sp,
                            color = Slate600
                        )
                    }
                    IconButton(onClick = { viewModel.deleteDocumentFromProject(doc.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Sil", tint = Slate600, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // 5. Score Card
        item {
            ScoreCard(score = score)
        }

        // 6. Findings Breakdown & Filters
        item {
            Column {
                Text(
                    text = "ÖN KONTROL VE ÇELİŞKİ BULGULARI (${allFindings.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate600
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Severity Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = activeSeverityFilter == null,
                            onClick = { viewModel.setSeverityFilter(null) },
                            label = { Text("Tümü (${allFindings.size})", fontSize = 11.sp) }
                        )
                    }
                    val critCount = allFindings.count { it.severity == Severity.CRITICAL }
                    val highCount = allFindings.count { it.severity == Severity.HIGH }
                    val warnCount = allFindings.count { it.severity == Severity.WARNING }
                    val sugCount = allFindings.count { it.severity == Severity.SUGGESTION }

                    if (critCount > 0) {
                        item {
                            FilterChip(
                                selected = activeSeverityFilter == Severity.CRITICAL,
                                onClick = {
                                    viewModel.setSeverityFilter(if (activeSeverityFilter == Severity.CRITICAL) null else Severity.CRITICAL)
                                },
                                label = { Text("🔴 Kritik ($critCount)", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                    if (highCount > 0) {
                        item {
                            FilterChip(
                                selected = activeSeverityFilter == Severity.HIGH,
                                onClick = {
                                    viewModel.setSeverityFilter(if (activeSeverityFilter == Severity.HIGH) null else Severity.HIGH)
                                },
                                label = { Text("🟠 Yüksek ($highCount)", fontSize = 11.sp) }
                            )
                        }
                    }
                    if (warnCount > 0) {
                        item {
                            FilterChip(
                                selected = activeSeverityFilter == Severity.WARNING,
                                onClick = {
                                    viewModel.setSeverityFilter(if (activeSeverityFilter == Severity.WARNING) null else Severity.WARNING)
                                },
                                label = { Text("🟡 Dikkat ($warnCount)", fontSize = 11.sp) }
                            )
                        }
                    }
                    if (sugCount > 0) {
                        item {
                            FilterChip(
                                selected = activeSeverityFilter == Severity.SUGGESTION,
                                onClick = {
                                    viewModel.setSeverityFilter(if (activeSeverityFilter == Severity.SUGGESTION) null else Severity.SUGGESTION)
                                },
                                label = { Text("🔵 Öneri ($sugCount)", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // 7. Findings List
        if (filteredFindings.isEmpty()) {
            item {
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text("🟢", fontSize = 28.sp)
                        Text(
                            text = "Seçili filtrede herhangi bir risk veya çelişki bulunamadı.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                    }
                }
            }
        } else {
            items(filteredFindings) { finding ->
                FindingCard(finding = finding)
            }
        }

        // 8. Bottom Disclaimer
        item {
            DisclaimerBanner()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Add Document Dialog
    if (showAddDocDialog) {
        AddDocumentDialog(
            onDismiss = { showAddDocDialog = false },
            onAdd = { name, type, content ->
                viewModel.addDocumentToActiveProject(name, type, content)
                showAddDocDialog = false
            }
        )
    }

    // View Document Dialog
    if (selectedDocToView != null) {
        val doc = selectedDocToView!!
        AlertDialog(
            onDismissRequest = { selectedDocToView = null },
            title = {
                Text(text = doc.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    Text(
                        text = "Tür: ${doc.documentType.title} • ${doc.wordCount} Kelime",
                        fontSize = 12.sp,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Slate100,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().height(260.dp)
                    ) {
                        LazyColumn(modifier = Modifier.padding(10.dp)) {
                            item {
                                Text(
                                    text = doc.rawContent,
                                    fontSize = 12.sp,
                                    color = Navy900,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDocToView = null }) {
                    Text("Kapat")
                }
            }
        )
    }

    // Report Export Dialog
    if (showReportDialog) {
        ReportExportDialog(
            project = project,
            documents = documents,
            findings = allFindings,
            score = score,
            onDismiss = { showReportDialog = false },
            onSave = {
                viewModel.saveCurrentProjectReport()
                showReportDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDocumentDialog(
    onDismiss: () -> Unit,
    onAdd: (String, DocumentType, String) -> Unit
) {
    var docName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(DocumentType.TECHNICAL_SPEC) }
    var content by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("İhale Dosyasına Doküman Ekle", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = docName,
                    onValueChange = { docName = it },
                    label = { Text("Belge Adı (Örn: Teknik_Sartname.txt)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_doc_name")
                )

                // Document Type
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedType.title,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Belge Türü") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        DocumentType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.title) },
                                onClick = {
                                    selectedType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Belge İçeriği") },
                    placeholder = { Text("Belge metnini buraya yapıştırınız...") },
                    minLines = 6,
                    maxLines = 10,
                    modifier = Modifier.fillMaxWidth().testTag("input_doc_content")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        val finalName = if (docName.isBlank()) "${selectedType.title}.txt" else docName
                        onAdd(finalName, selectedType, content)
                    }
                },
                modifier = Modifier.testTag("btn_confirm_add_doc")
            ) {
                Text("Ekle ve Analiz Et")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}
