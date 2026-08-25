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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TenderProjectEntity
import com.example.domain.engine.SampleDataGenerator
import com.example.domain.model.TenderProcedure
import com.example.domain.model.TenderType
import com.example.ui.components.DisclaimerBanner
import com.example.ui.theme.BrandBlue600
import com.example.ui.theme.BrandBlue700
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate600
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.allProjects.collectAsState()
    var showNewProjectDialog by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize().testTag("home_screen_container")
    ) {
        // 1. Header Banner
        item {
            Surface(
                color = Navy900,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KAMU İHALE VE BELGE DENETİM",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = Color.White
                            )
                        }
                        Surface(
                            color = BrandBlue700,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "LOCAL-FIRST / %100 GÜVENLİ",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "İhale Şartnameleri ve Belge Denetim Platformu",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Dokümanlarınızı 4734/4735 Sayılı Kanunlar, marka/model rekabet riskleri, süre/garanti uyuşmazlıkları ve Türkçe resmi yazışma kuralları açısından denetleyin.",
                        fontSize = 13.sp,
                        color = Slate200,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // 2. Primary Core Actions Grid (2 Seconds Comprehension)
        item {
            Text(
                text = "NE YAPMAK İSTİYORSUNUZ?",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = Slate600
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Card 1: Tek Belge Denetimi
                ActionMenuCard(
                    title = "Tek Belge Denetimi",
                    subtitle = "Teknik Şartname, Rapor veya Metin (Yazım, Biçim, Marka ve KVKK Redaction)",
                    icon = Icons.Default.Description,
                    badgeText = "Hızlı Analiz",
                    onClick = { viewModel.navigateTo(AppScreen.SINGLE_DOC_AUDIT) },
                    testTag = "action_single_doc"
                )

                // Card 2: İhale Dosyası Projesi
                ActionMenuCard(
                    title = "İhale Dosyası Denetimi (Çapraz Kontrol)",
                    subtitle = "İlan + İdari + Teknik + Sözleşme + Teklif Cetveli Arası Çelişki Matrisi",
                    icon = Icons.Default.Folder,
                    badgeText = "Tüm İhale Paketi",
                    onClick = { showNewProjectDialog = true },
                    testTag = "action_new_tender_project"
                )

                // Card 3: Belgeleri Karşılaştır
                ActionMenuCard(
                    title = "Belgeleri Karşılaştır",
                    subtitle = "İki şartname veya sözleşme arasındaki süre, garanti ve cezai şart farkları",
                    icon = Icons.Default.CompareArrows,
                    badgeText = "Semantik Diff",
                    onClick = { viewModel.navigateTo(AppScreen.CROSS_COMPARE) },
                    testTag = "action_cross_compare"
                )
            }
        }

        // 3. One-Click Sample Demos (Real-world scenarios)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate100),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("card_sample_demos")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = BrandBlue700,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HIZLI TEST: GERÇEKÇİ ÇELİŞKİLİ İHALE PAKETLERİ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sistemin marka/model ihlali, 24 ay vs 12 ay garanti çelişkisi ve cetvel uyuşmazlıklarını nasıl yakaladığını anında test edin:",
                        fontSize = 12.sp,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val samples = SampleDataGenerator.getSampleTenderPackages()
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { viewModel.loadSampleTenderProject(samples[0]) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("btn_sample_tcdd")
                        ) {
                            Text("🚆 TCDD Ray Bakım İhalesi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { viewModel.loadSampleTenderProject(samples[1]) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("btn_sample_saglik")
                        ) {
                            Text("🏥 Hastane Cihaz İhalesi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 4. Saved Projects Section
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "KAYITLI İHALE PROJELERİ (${projects.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = Slate600
                )
                TextButton(
                    onClick = { showNewProjectDialog = true },
                    modifier = Modifier.testTag("btn_create_project_header")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Yeni İhale", fontSize = 12.sp)
                }
            }
        }

        if (projects.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Slate200, RoundedCornerShape(12.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = Slate600,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Henüz bir ihale dosyası oluşturmadınız",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate600
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Yukarıdaki 'Yeni İhale' butonuna basarak veya örnek paketleri yükleyerek başlayabilirsiniz.",
                            fontSize = 12.sp,
                            color = Slate600,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        } else {
            items(projects) { project ->
                ProjectItemCard(
                    project = project,
                    onOpen = {
                        viewModel.selectProject(project.id)
                        viewModel.navigateTo(AppScreen.TENDER_PROJECT_AUDIT)
                    },
                    onDelete = { viewModel.deleteProject(project.id) }
                )
            }
        }

        // 5. Utility Shortcuts (Rules, Legislation, Reports)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "YÖNETİM & MEVZUAT REHBERİ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Slate600
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SecondaryShortcutCard(
                    title = "Kurallarım",
                    subtitle = "Kuruma özel kurallar",
                    icon = Icons.Default.Rule,
                    onClick = { viewModel.navigateTo(AppScreen.CUSTOM_RULES) },
                    modifier = Modifier.weight(1f),
                    testTag = "shortcut_custom_rules"
                )
                SecondaryShortcutCard(
                    title = "Mevzuat & KİK",
                    subtitle = "4734/4735 ve Emsaller",
                    icon = Icons.Default.Gavel,
                    onClick = { viewModel.navigateTo(AppScreen.LEGISLATION_BROWSER) },
                    modifier = Modifier.weight(1f),
                    testTag = "shortcut_legislation"
                )
            }
        }

        // 6. Disclaimer Banner
        item {
            DisclaimerBanner()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // New Project Dialog
    if (showNewProjectDialog) {
        NewProjectDialog(
            onDismiss = { showNewProjectDialog = false },
            onCreate = { no, title, type, proc, inst ->
                viewModel.createNewProject(no, title, type, proc, inst)
                showNewProjectDialog = false
            }
        )
    }
}

@Composable
fun ActionMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeText: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .background(BrandBlue700.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = BrandBlue700, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Surface(
                        color = Slate100,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(text = badgeText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandBlue700, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 12.sp, color = Slate600, lineHeight = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Slate600, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun SecondaryShortcutCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .border(1.dp, Slate200, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BrandBlue700, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontSize = 11.sp, color = Slate600)
            }
        }
    }
}

@Composable
fun ProjectItemCard(
    project: TenderProjectEntity,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
            .clickable(onClick = onOpen)
            .testTag("project_item_${project.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = BrandBlue700.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "İKN: ${project.tenderNumber}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue700,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${project.tenderType.displayName}",
                        fontSize = 12.sp,
                        color = Slate600
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = project.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = project.institutionName,
                    fontSize = 12.sp,
                    color = Slate600
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Sil", tint = Slate600, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, TenderType, TenderProcedure, String) -> Unit
) {
    var tenderNo by remember { mutableStateOf("2026/") }
    var title by remember { mutableStateOf("") }
    var institution by remember { mutableStateOf("T.C. Genel Müdürlüğü") }
    var selectedType by remember { mutableStateOf(TenderType.GOODS) }
    var selectedProcedure by remember { mutableStateOf(TenderProcedure.OPEN) }

    var typeExpanded by remember { mutableStateOf(false) }
    var procExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Yeni İhale Dosyası Oluştur", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = tenderNo,
                    onValueChange = { tenderNo = it },
                    label = { Text("İhale Kayıt No (İKN)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_tender_no")
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("İhale Adı / Konusu") },
                    placeholder = { Text("Örn: Tıbbi Cihaz ve Sarf Malzeme Alımı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_tender_title")
                )

                OutlinedTextField(
                    value = institution,
                    onValueChange = { institution = it },
                    label = { Text("İdare / Kurum Adı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_tender_institution")
                )

                // Tender Type Selector
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("İhale Türü") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        TenderType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    selectedType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                // Tender Procedure Selector
                ExposedDropdownMenuBox(
                    expanded = procExpanded,
                    onExpandedChange = { procExpanded = !procExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedProcedure.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("İhale Usulü") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = procExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = procExpanded,
                        onDismissRequest = { procExpanded = false }
                    ) {
                        TenderProcedure.values().forEach { proc ->
                            DropdownMenuItem(
                                text = { Text(proc.displayName) },
                                onClick = {
                                    selectedProcedure = proc
                                    procExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(tenderNo, title, selectedType, selectedProcedure, institution)
                    }
                },
                modifier = Modifier.testTag("btn_confirm_create_project")
            ) {
                Text("Dosyayı Oluştur")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
