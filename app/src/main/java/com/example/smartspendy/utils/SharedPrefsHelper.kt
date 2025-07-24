package com.example.smartspendy.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.smartspendy.model.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class SharedPrefsHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("SmartSpendyPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_TRANSACTIONS = "transactions"
        private const val KEY_BUDGET = "budget"
        private const val KEY_CURRENCY = "currency"
    }

    // Save and retrieve transactions
    fun saveTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        prefs.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    fun getTransactions(): List<Transaction> {
        val json = prefs.getString(KEY_TRANSACTIONS, null)
        return if (json != null) {
            val type = object : TypeToken<List<Transaction>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    // Save and retrieve monthly budget
    fun saveBudget(budget: Double) {
        prefs.edit().putFloat(KEY_BUDGET, budget.toFloat()).apply()
    }

    fun getBudget(): Double {
        return prefs.getFloat(KEY_BUDGET, 0f).toDouble()
    }

    // Save and retrieve currency
    fun saveCurrency(currency: String) {
        prefs.edit().putString(KEY_CURRENCY, currency).apply()
    }

    fun getCurrency(): String {
        return prefs.getString(KEY_CURRENCY, "Rs") ?: "Rs"
    }
}