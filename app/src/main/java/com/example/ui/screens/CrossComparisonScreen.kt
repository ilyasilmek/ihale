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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.domain.engine.LegislationRuleEngine
import com.example.domain.model.DocumentType
import com.example.ui.theme.BrandBlue700
import com.example.ui.theme.Navy900
import com.example.ui.theme.SeverityCritical
import com.example.ui.theme.SeveritySuccess
import com.example.ui.theme.SeverityWarning
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrossComparisonScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var docAName by remember { mutableStateOf("İdari Şartname") }
    var docAType by remember { mutableStateOf(DocumentType.ADMINISTRATIVE_SPEC) }
    var docAContent by remember {
        mutableStateOf(
            """
                İDARİ ŞARTNAME
                Madde 1 - İşin Teslim Süresi
                İşe başlama tarihinden itibaren 45 takvim günü içinde teslim edilecektir.
                
                Madde 2 - Garanti Şartları
                Mallar en az 12 ay süreli garanti kapsamında olacaktır.
                
                Madde 3 - Gecikme Cezası
                Günlük gecikme cezası %0.05 oranındadır.
            """.trimIndent()
        )
    }

    var docBName by remember { mutableStateOf("Teknik Şartname") }
    var docBType by remember { mutableStateOf(DocumentType.TECHNICAL_SPEC) }
    var docBContent by remember {
        mutableStateOf(
            """
                TEKNİK ŞARTNAME
                Madde 1 - Donanım Özellikleri
                Cihazlar Siemens S7 kontrol ünitesi ile çalışacaktır.
                
                Madde 2 - Garanti Koşulları
                Tüm donanımlar 24 ay süreli garantiye sahip olmalıdır.
                
                Madde 3 - Teslimat
                Teslimat 30 takvim günü içinde yapılacaktır.
            """.trimIndent()
        )
    }

    val paramsA = remember(docAContent, docAType) {
        LegislationRuleEngine.extractParameters(docAContent, docAType)
    }
    val paramsB = remember(docBContent, docBType) {
        LegislationRuleEngine.extractParameters(docBContent, docBType)
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier.fillMaxSize().testTag("cross_compare_screen")
    ) {
        // Top Nav
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Belgeleri Karşılaştır",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "İki Belge Arasındaki Süre, Garanti ve Madde Çelişkileri",
                        fontSize = 12.sp,
                        color = Slate600
                    )
                }
            }
        }

        // Comparison Diff Matrix
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Slate200, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "PARAMETRİK ÇELİŞKİ VE UYUM TABLOSU",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate600
                        )
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = null,
                            tint = BrandBlue700,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row 1: Garanti
                    DiffRow(
                        parameterName = "Garanti Süresi",
                        valA = paramsA.warrantyMonths?.let { "$it Ay" } ?: "Belirtilmemiş",
                        valB = paramsB.warrantyMonths?.let { "$it Ay" } ?: "Belirtilmemiş",
                        isMismatch = paramsA.warrantyMonths != null && paramsB.warrantyMonths != null && paramsA.warrantyMonths != paramsB.warrantyMonths
                    )

                    Divider(color = Slate200, modifier = Modifier.padding(vertical = 8.dp))

                    // Row 2: Teslim Süresi
                    DiffRow(
                        parameterName = "Teslim Süresi",
                        valA = paramsA.deliveryDays?.let { "$it Gün" } ?: "Belirtilmemiş",
                        valB = paramsB.deliveryDays?.let { "$it Gün" } ?: "Belirtilmemiş",
                        isMismatch = paramsA.deliveryDays != null && paramsB.deliveryDays != null && paramsA.deliveryDays != paramsB.deliveryDays
                    )

                    Divider(color = Slate200, modifier = Modifier.padding(vertical = 8.dp))

                    // Row 3: Ceza Oranı
                    DiffRow(
                        parameterName = "Günlük Ceza Oranı",
                        valA = paramsA.penaltyRatePerDay?.let { "%$it" } ?: "Belirtilmemiş",
                        valB = paramsB.penaltyRatePerDay?.let { "%$it" } ?: "Belirtilmemiş",
                        isMismatch = paramsA.penaltyRatePerDay != null && paramsB.penaltyRatePerDay != null && paramsA.penaltyRatePerDay != paramsB.penaltyRatePerDay
                    )

                    Divider(color = Slate200, modifier = Modifier.padding(vertical = 8.dp))

                    // Row 4: Marka Sayısı
                    DiffRow(
                        parameterName = "Marka / Model Sayısı",
                        valA = "${paramsA.mentionedBrands.size} Marka",
                        valB = "${paramsB.mentionedBrands.size} Marka",
                        isMismatch = false
                    )
                }
            }
        }

        // Document A Input
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Slate200, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "1. BELGE: $docAName",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue700
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = docAContent,
                        onValueChange = { docAContent = it },
                        minLines = 4,
                        maxLines = 8,
                        modifier = Modifier.fillMaxWidth().testTag("input_diff_doc_a")
                    )
                }
            }
        }

        // Document B Input
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Slate200, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "2. BELGE: $docBName",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue700
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = docBContent,
                        onValueChange = { docBContent = it },
                        minLines = 4,
                        maxLines = 8,
                        modifier = Modifier.fillMaxWidth().testTag("input_diff_doc_b")
                    )
                }
            }
        }
    }
}

@Composable
fun DiffRow(
    parameterName: String,
    valA: String,
    valB: String,
    isMismatch: Boolean
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = parameterName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
            if (isMismatch) {
                Surface(
                    color = SeverityCritical.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "🔴 ÇELİŞKİ VAR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SeverityCritical,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            } else {
                Surface(
                    color = SeveritySuccess.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "🟢 UYUMLU",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SeveritySuccess,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "1. Belge: $valA", fontSize = 12.sp, color = Slate600)
            Text(text = "2. Belge: $valB", fontSize = 12.sp, color = Slate600)
        }
    }
}
