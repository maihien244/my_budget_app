package com.example.mybudget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybudget.data.local.AppDatabase
import com.example.mybudget.data.repository.ExpenseRepository
import com.example.mybudget.data.repository.UserPreferencesRepository
import com.example.mybudget.ui.navigation.AppNavigation
import com.example.mybudget.ui.theme.MyBudgetTheme
import com.example.mybudget.viewmodel.ExpenseViewModel
import com.example.mybudget.viewmodel.ExpenseViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext)
        val repository = ExpenseRepository(db.expenseDao())
        val preferencesRepository = UserPreferencesRepository(applicationContext)
        val factory = ExpenseViewModelFactory(repository, preferencesRepository)

        setContent {
            MyBudgetTheme {
                val viewModel: ExpenseViewModel = viewModel(factory = factory)
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}