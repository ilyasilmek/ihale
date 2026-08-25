package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.LegislationDatabase
import com.example.domain.engine.LegislationItem
import com.example.ui.theme.BrandBlue700
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.Navy900
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LegislationBrowserScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLawFilter by remember { mutableStateOf<String?>(null) }

    val allArticles = LegislationDatabase.articles
    val filteredArticles = allArticles.filter { item ->
        val matchSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.summaryText.contains(searchQuery, ignoreCase = true) ||
                item.articleNumber.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true)

        val matchLaw = selectedLawFilter == null || item.lawCode.contains(selectedLawFilter!!, ignoreCase = true)

        matchSearch && matchLaw
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier.fillMaxSize().testTag("legislation_browser_screen")
    ) {
        // Nav Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Mevzuat ve KİK Rehberi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "4734, 4735 Sayılı Kanunlar ve Emsal Kararlar",
                        fontSize = 12.sp,
                        color = Slate600
                    )
                }
            }
        }

        // Search Field
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Mevzuatta veya KİK kararlarında ara...") },
                placeholder = { Text("Örn: Marka, Madde 12, Teminat, Garanti...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate600) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_legislation_search")
            )
        }

        // Law Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedLawFilter == null,
                        onClick = { selectedLawFilter = null },
                        label = { Text("Tümü (${allArticles.size})") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedLawFilter == "4734",
                        onClick = { selectedLawFilter = if (selectedLawFilter == "4734") null else "4734" },
                        label = { Text("4734 Sayılı KİK") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedLawFilter == "4735",
                        onClick = { selectedLawFilter = if (selectedLawFilter == "4735") null else "4735" },
                        label = { Text("4735 Sayılı Sözleşmeler") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedLawFilter == "YÖNETMELİK",
                        onClick = { selectedLawFilter = if (selectedLawFilter == "YÖNETMELİK") null else "YÖNETMELİK" },
                        label = { Text("EKAP Yönetmeliği") }
                    )
                }
            }
        }

        // Items
        items(filteredArticles) { item ->
            LegislationCard(item = item)
        }
    }
}

@Composable
fun LegislationCard(item: LegislationItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = BrandBlue700.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${item.lawCode} - ${item.articleNumber}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue700,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = item.category,
                    fontSize = 11.sp,
                    color = Slate600
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.summaryText,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Slate200)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Kanun Metni:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
                Text(
                    text = item.fullText,
                    fontSize = 12.sp,
                    color = Slate600,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = Slate100,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = BrandBlue700, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "KİK ve Yargı Uygulaması Notu:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandBlue700
                            )
                        }
                        Text(
                            text = item.kikGuidanceNotes,
                            fontSize = 12.sp,
                            color = Navy900,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
