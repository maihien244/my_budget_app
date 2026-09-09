package com.example.mybudget.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mybudget.data.repository.Expense
import com.example.mybudget.ui.components.CategoryFilterBar
import com.example.mybudget.ui.components.EmptyExpenseState
import com.example.mybudget.ui.components.ExpenseItemCard
import com.example.mybudget.ui.components.MonthSelectorHeader
import com.example.mybudget.ui.components.OverviewCard
import com.example.mybudget.ui.components.SetBudgetDialog
import com.example.mybudget.ui.theme.MyBudgetTheme
import com.example.mybudget.utils.CurrencyUtils
import com.example.mybudget.viewmodel.ExpenseViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: ExpenseViewModel
) {
    val selectedCal by viewModel.selectedCalendar.collectAsState()
    val filteredExpenses by viewModel.filteredExpenses.collectAsState()
    val totalAmount by viewModel.monthlyTotal.collectAsState()
    val monthlyBudget by viewModel.monthlyBudget.collectAsState()
    val salaryCycleStartDay by viewModel.salaryCycleStartDay.collectAsState()
    val selectedFilter by viewModel.selectedCategoryFilter.collectAsState()

    val (periodStart, periodEnd) = CurrencyUtils.getSalaryCyclePeriod(selectedCal, salaryCycleStartDay)
    val dateRangeStr = CurrencyUtils.formatDateRange(periodStart, periodEnd)

    HomeScreenContent(
        selectedCal = selectedCal,
        dateRangeStr = dateRangeStr,
        filteredExpenses = filteredExpenses,
        totalAmount = totalAmount,
        monthlyBudget = monthlyBudget,
        selectedFilter = selectedFilter,
        onPreviousMonth = { viewModel.previousMonth() },
        onNextMonth = { viewModel.nextMonth() },
        onSelectFilter = { viewModel.selectCategoryFilter(it) },
        onDeleteExpense = { viewModel.deleteExpense(it) },
        onSetMonthlyBudget = { viewModel.setMonthlyBudget(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    selectedCal: Calendar,
    dateRangeStr: String,
    filteredExpenses: List<Expense>,
    totalAmount: Double,
    monthlyBudget: Double,
    selectedFilter: String?,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectFilter: (String?) -> Unit,
    onDeleteExpense: (Expense) -> Unit,
    onSetMonthlyBudget: (Double) -> Unit
) {
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Month Selector Header Component
            MonthSelectorHeader(
                currentMonthStr = CurrencyUtils.formatMonthYear(selectedCal),
                dateRangeStr = dateRangeStr,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Monthly Overview Balance Card Component
            OverviewCard(
                totalAmount = totalAmount,
                monthlyBudget = monthlyBudget,
                transactionCount = filteredExpenses.size,
                onEditBudgetClick = { showBudgetDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Filter Bar Component
            CategoryFilterBar(
                selectedFilter = selectedFilter,
                onSelectFilter = onSelectFilter
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Transactions Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Danh sách chi tiêu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${filteredExpenses.size} giao dịch",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expense List
            if (filteredExpenses.isEmpty()) {
                EmptyExpenseState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredExpenses, key = { it.id }) { expense ->
                        ExpenseItemCard(
                            expense = expense,
                            onDeleteClick = { expenseToDelete = expense }
                        )
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        expenseToDelete?.let { expense ->
            AlertDialog(
                onDismissRequest = { expenseToDelete = null },
                title = { Text("Xác nhận xóa") },
                text = { Text("Bạn có chắc chắn muốn xóa khoản chi tiêu \"${expense.note.ifBlank { expense.category }}\" không?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteExpense(expense)
                            expenseToDelete = null
                        }
                    ) {
                        Text("Xóa", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { expenseToDelete = null }) {
                        Text("Hủy")
                    }
                }
            )
        }

        // Set Budget Dialog Component
        if (showBudgetDialog) {
            SetBudgetDialog(
                currentBudget = monthlyBudget,
                onDismiss = { showBudgetDialog = false },
                onSaveBudget = { newBudget ->
                    onSetMonthlyBudget(newBudget)
                    showBudgetDialog = false
                }
            )
        }
    }

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MyBudgetTheme {
        HomeScreenContent(
            selectedCal = Calendar.getInstance(),
            dateRangeStr = "05/09/2026 - 04/10/2026",
            filteredExpenses = listOf(
                Expense(1, 45000.0, "Ăn uống", System.currentTimeMillis(), "Ăn trưa phở bò"),
                Expense(2, 200000.0, "Mua sắm", System.currentTimeMillis() - 86400000, "Áo sơ mi"),
                Expense(3, 50000.0, "Di chuyển", System.currentTimeMillis() - 172800000, "Đổ xăng")
            ),
            totalAmount = 295000.0,
            monthlyBudget = 5000000.0,
            selectedFilter = null,
            onPreviousMonth = {},
            onNextMonth = {},
            onSelectFilter = {},
            onDeleteExpense = {},
            onSetMonthlyBudget = {}
        )
    }
}
