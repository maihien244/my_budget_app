package com.example.mybudget.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mybudget.data.model.CategoryProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterBar(
    selectedFilter: String?,
    onSelectFilter: (String?) -> Unit
) {
    val filters = remember {
        listOf("Tất cả") + CategoryProvider.categories.map { it.name }
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(filters) { categoryName ->
            val isSelected = if (categoryName == "Tất cả") {
                selectedFilter.isNullOrBlank() || selectedFilter == "Tất cả"
            } else {
                selectedFilter == categoryName
            }

            FilterChip(
                selected = isSelected,
                onClick = {
                    if (categoryName == "Tất cả") {
                        onSelectFilter(null)
                    } else {
                        onSelectFilter(if (isSelected) null else categoryName)
                    }
                },
                label = { Text(categoryName, fontSize = 13.sp) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}
