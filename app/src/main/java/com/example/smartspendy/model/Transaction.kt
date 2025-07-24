package com.example.smartspendy.model

import java.util.Date

data class Transaction(
    val id: Int,
    val title: String,
    val amount: Double,
    val categoryId: Int,
    val date: Date,
    val type: String // "Income" or "Expense"
)