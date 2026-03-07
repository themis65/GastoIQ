package com.example.gastoiq.ui.model

data class ExpenseUiModel(
    val id: Long,
    val amount: String,
    val category: String,
    val date: String,
    val description: String
)