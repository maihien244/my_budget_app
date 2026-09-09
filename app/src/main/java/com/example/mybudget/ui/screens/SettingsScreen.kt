package com.example.mybudget.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mybudget.ui.components.SetBudgetDialog
import com.example.mybudget.ui.components.SetSalaryDayDialog
import com.example.mybudget.utils.CurrencyUtils
import com.example.mybudget.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ExpenseViewModel
) {
    val monthlyBudget by viewModel.monthlyBudget.collectAsState()
    val salaryCycleStartDay by viewModel.salaryCycleStartDay.collectAsState()

    var showBudgetDialog by remember { mutableStateOf(false) }
    var showSalaryDayDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Quản lý ngân sách & kỳ lương",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Fixed Monthly Budget Setting Card
        SettingItemCard(
            title = "Ngân sách cố định hàng tháng",
            subtitle = if (monthlyBudget > 0) CurrencyUtils.formatVnd(monthlyBudget) else "Chưa thiết lập",
            icon = Icons.Default.AccountBalanceWallet,
            onClick = { showBudgetDialog = true }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Salary Cycle Start Day Card
        SettingItemCard(
            title = "Ngày bắt đầu kỳ lương (Kỳ chi tiêu)",
            subtitle = "Ngày $salaryCycleStartDay hàng tháng",
            icon = Icons.Default.CalendarMonth,
            onClick = { showSalaryDayDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Thông tin ứng dụng",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        SettingItemCard(
            title = "Lưu trữ dữ liệu",
            subtitle = "Máy cục bộ (Room Database)",
            icon = Icons.Default.Storage,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingItemCard(
            title = "Phiên bản ứng dụng",
            subtitle = "MyBudget v1.0.0",
            icon = Icons.Default.Info,
            onClick = {}
        )
    }

    if (showBudgetDialog) {
        SetBudgetDialog(
            currentBudget = monthlyBudget,
            onDismiss = { showBudgetDialog = false },
            onSaveBudget = { newBudget ->
                viewModel.setMonthlyBudget(newBudget)
                showBudgetDialog = false
            }
        )
    }

    if (showSalaryDayDialog) {
        SetSalaryDayDialog(
            currentDay = salaryCycleStartDay,
            onDismiss = { showSalaryDayDialog = false },
            onSaveDay = { newDay ->
                viewModel.setSalaryCycleStartDay(newDay)
                showSalaryDayDialog = false
            }
        )
    }
}

@Composable
fun SettingItemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
