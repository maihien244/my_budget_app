package com.example.mybudget.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserPreferencesRepository(private val context: Context) {
    private val MONTHLY_BUDGET_KEY = doublePreferencesKey("monthly_budget")
    private val SALARY_CYCLE_START_DAY_KEY = intPreferencesKey("salary_cycle_start_day")

    val monthlyBudget: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[MONTHLY_BUDGET_KEY] ?: 0.0
    }

    val salaryCycleStartDay: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[SALARY_CYCLE_START_DAY_KEY] ?: 1
    }

    suspend fun setMonthlyBudget(amount: Double) {
        context.dataStore.edit { preferences ->
            preferences[MONTHLY_BUDGET_KEY] = amount
        }
    }

    suspend fun setSalaryCycleStartDay(day: Int) {
        context.dataStore.edit { preferences ->
            preferences[SALARY_CYCLE_START_DAY_KEY] = day.coerceIn(1, 28)
        }
    }
}
