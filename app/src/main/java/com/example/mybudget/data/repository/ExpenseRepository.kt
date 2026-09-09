package com.example.mybudget.data.repository

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    fun getExpensesForMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesForMonth(startOfMonth, endOfMonth)
    }

    fun getMonthlyTotal(startOfMonth: Long, endOfMonth: Long): Flow<Double?> {
        return expenseDao.getMonthlyTotal(startOfMonth, endOfMonth)
    }

    suspend fun insert(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun delete(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }
}
