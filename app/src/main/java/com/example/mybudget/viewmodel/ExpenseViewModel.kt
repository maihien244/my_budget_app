package com.example.mybudget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mybudget.data.repository.Expense
import com.example.mybudget.data.repository.ExpenseRepository
import com.example.mybudget.data.repository.UserPreferencesRepository
import com.example.mybudget.utils.CurrencyUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpenseViewModel(
    private val repository: ExpenseRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _selectedCalendar = MutableStateFlow(CurrencyUtils.getInitialCalendarForToday(1))
    val selectedCalendar: StateFlow<Calendar> = _selectedCalendar.asStateFlow()

    private var isInitialMonthSet = false

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    val monthlyBudget: StateFlow<Double> = preferencesRepository.monthlyBudget.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val salaryCycleStartDay: StateFlow<Int> = preferencesRepository.salaryCycleStartDay.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 1
    )

    init {
        viewModelScope.launch {
            preferencesRepository.salaryCycleStartDay.collect { startDay ->
                if (!isInitialMonthSet) {
                    _selectedCalendar.value = CurrencyUtils.getInitialCalendarForToday(startDay)
                    isInitialMonthSet = true
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val monthlyExpenses: StateFlow<List<Expense>> = combine(
        _selectedCalendar,
        preferencesRepository.salaryCycleStartDay
    ) { cal, startDay ->
        Pair(cal, startDay)
    }.flatMapLatest { (cal, startDay) ->
        val (start, end) = CurrencyUtils.getSalaryCyclePeriod(cal, startDay)
        repository.getExpensesForMonth(start, end)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredExpenses: StateFlow<List<Expense>> = combine(
        monthlyExpenses,
        _selectedCategoryFilter
    ) { expenses, category ->
        if (category.isNullOrBlank() || category == "Tất cả") {
            expenses
        } else {
            expenses.filter { it.category.equals(category, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val monthlyTotal: StateFlow<Double> = monthlyExpenses.combine(monthlyExpenses) { expenses, _ ->
        expenses.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun previousMonth() {
        val newCal = _selectedCalendar.value.clone() as Calendar
        newCal.add(Calendar.MONTH, -1)
        _selectedCalendar.value = newCal
    }

    fun nextMonth() {
        val newCal = _selectedCalendar.value.clone() as Calendar
        newCal.add(Calendar.MONTH, 1)
        _selectedCalendar.value = newCal
    }

    fun selectCategoryFilter(category: String?) {
        _selectedCategoryFilter.value = category
    }

    fun setMonthlyBudget(amount: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.setMonthlyBudget(amount)
        }
    }

    fun setSalaryCycleStartDay(day: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.setSalaryCycleStartDay(day)
        }
        _selectedCalendar.value = CurrencyUtils.getInitialCalendarForToday(day)
    }

    fun addExpense(amount: Double, category: String, date: Long, note: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val expense = Expense(
                amount = amount,
                category = category,
                date = date,
                note = note
            )
            repository.insert(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(expense)
        }
    }
}

class ExpenseViewModelFactory(
    private val repository: ExpenseRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repository, preferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}