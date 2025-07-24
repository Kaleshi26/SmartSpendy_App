package com.example.smartspendy.model

data class BudgetItem(
    val categoryId: Int,
    val startDate: String,
    val endDate: String,
    val spentAmount: Double,
    val totalAmount: Double,
    val residualAmount: Double,
    val progress: Int,
    val period: String // "Weekly", "Monthly", "Yearly"
)