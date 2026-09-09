package com.example.mybudget.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ExpenseCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

object CategoryProvider {
    val categories = listOf(
        ExpenseCategory("food", "Ăn uống", Icons.Default.Restaurant, Color(0xFFFF6B6B)),
        ExpenseCategory("transport", "Di chuyển", Icons.Default.DirectionsCar, Color(0xFF4ECDC4)),
        ExpenseCategory("shopping", "Mua sắm", Icons.Default.ShoppingBag, Color(0xFFFFBE0B)),
        ExpenseCategory("bills", "Hóa đơn", Icons.Default.Receipt, Color(0xFF45B7D1)),
        ExpenseCategory("entertainment", "Giải trí", Icons.Default.Movie, Color(0xFF96CEB4)),
        ExpenseCategory("health", "Sức khỏe", Icons.Default.LocalHospital, Color(0xFFFF9F1C)),
        ExpenseCategory("other", "Khác", Icons.Default.MoreHoriz, Color(0xFFA8DADC))
    )

    fun getCategoryByName(name: String): ExpenseCategory {
        return categories.find { it.name.equals(name, ignoreCase = true) }
            ?: categories.last()
    }
}
