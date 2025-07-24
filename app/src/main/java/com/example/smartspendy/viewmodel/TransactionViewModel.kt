package com.example.smartspendy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.smartspendy.model.Category
import com.example.smartspendy.model.Transaction
import com.example.smartspendy.utils.NotificationHelper
import com.example.smartspendy.utils.SharedPrefsHelper
import java.util.Date

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefsHelper = SharedPrefsHelper(application)
    private val notificationHelper = NotificationHelper(application)

    private val _transactions = MutableLiveData<List<Transaction>>(sharedPrefsHelper.getTransactions())
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private val _budget = MutableLiveData(sharedPrefsHelper.getBudget())
    val budget: LiveData<Double> get() = _budget

    private val _currency = MutableLiveData(sharedPrefsHelper.getCurrency())
    val currency: LiveData<String> get() = _currency

    private val _categorySummary = MutableLiveData<List<CategorySummary>>()
    val categorySummary: LiveData<List<CategorySummary>> get() = _categorySummary

    private var nextTransactionId: Int = sharedPrefsHelper.getTransactions().maxByOrNull { it.id }?.id?.plus(1) ?: 1

    init {
        refreshCategorySummary()
    }

    fun addTransaction(title: String, amount: Double, categoryId: Int, date: Date, type: String) {
        val transaction = Transaction(nextTransactionId++, title, amount, categoryId, date, type)
        val updatedList = _transactions.value.orEmpty().toMutableList().apply { add(transaction) }
        _transactions.value = updatedList
        sharedPrefsHelper.saveTransactions(updatedList)
        checkBudgetStatus()
        refreshCategorySummary()
    }

    fun updateTransaction(id: Int, title: String, amount: Double, categoryId: Int, date: Date, type: String) {
        val updatedList = _transactions.value.orEmpty().toMutableList()
        val index = updatedList.indexOfFirst { it.id == id }
        if (index != -1) {
            updatedList[index] = Transaction(id, title, amount, categoryId, date, type)
            _transactions.value = updatedList
            sharedPrefsHelper.saveTransactions(updatedList)
            checkBudgetStatus()
            refreshCategorySummary()
        }
    }

    fun deleteTransaction(id: Int) {
        val updatedList = _transactions.value.orEmpty().toMutableList()
        updatedList.removeIf { it.id == id }
        _transactions.value = updatedList
        sharedPrefsHelper.saveTransactions(updatedList)
        checkBudgetStatus()
        refreshCategorySummary()
    }

    fun setBudget(budget: Double) {
        _budget.value = budget
        sharedPrefsHelper.saveBudget(budget)
        checkBudgetStatus()
    }

    fun setCurrency(currency: String) {
        _currency.value = currency
        sharedPrefsHelper.saveCurrency(currency)
    }

    fun getTotalExpense(): Double {
        return _transactions.value?.filter { it.type == "Expense" }?.sumOf { it.amount } ?: 0.0
    }

    fun getTotalIncome(): Double {
        return _transactions.value?.filter { it.type == "Income" }?.sumOf { it.amount } ?: 0.0
    }

    fun getBudgetProgress(): Int {
        val budget = _budget.value ?: 0.0
        val totalExpense = getTotalExpense()
        return if (budget > 0) ((totalExpense / budget) * 100).toInt().coerceIn(0, 100) else 0
    }

    fun getExpensesByCategory(): Map<Int, Double> {
        return _transactions.value?.filter { it.type == "Expense" }
            ?.groupBy { it.categoryId }
            ?.mapValues { entry -> entry.value.sumOf { it.amount } } ?: emptyMap()
    }

    private fun checkBudgetStatus() {
        val budget = _budget.value ?: 0.0
        val totalExpense = getTotalExpense()
        if (budget > 0) {
            when {
                totalExpense >= budget -> {
                    notificationHelper.sendBudgetAlert("You've exceeded your budget of ${currency.value} $budget!")
                }
                totalExpense >= budget * 0.9 -> {
                    notificationHelper.sendBudgetAlert("You're nearing your budget limit of ${currency.value} $budget!")
                }
            }
        }
    }

    fun sendDailyReminder() {
        notificationHelper.sendDailyReminder()
    }

    fun exportTransactions() {
        _transactions.value?.let { sharedPrefsHelper.saveTransactions(it) }
    }

    fun importTransactions() {
        _transactions.value = sharedPrefsHelper.getTransactions()
        refreshCategorySummary()
    }

    private fun refreshCategorySummary() {
        val expensesByCategory = getExpensesByCategory()
        val categories = listOf(
            Category(1, "Food"),
            Category(2, "Transport"),
            Category(3, "Bills"),
            Category(4, "Entertainment"),
            Category(5, "Other")
        )
        _categorySummary.value = categories.map { category ->
            val amount = expensesByCategory[category.id] ?: 0.0
            CategorySummary(category.name, amount)
        }.filter { it.totalAmount > 0.0 }
    }
}

data class CategorySummary(val name: String, val totalAmount: Double)
