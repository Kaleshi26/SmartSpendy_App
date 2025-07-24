package com.example.smartspendy.utils

import android.content.Context
import android.os.Environment
import com.example.smartspendy.model.Transaction
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileHelper(private val context: Context) {

    // Existing method for backup (assumed to save transactions to internal storage)
    fun saveTransactionsToFile(transactions: List<Transaction>) {
        val file = File(context.filesDir, "transactions_backup.txt")
        file.writeText(transactions.joinToString("\n") { transaction ->
            "${transaction.id},${transaction.title},${transaction.amount},${transaction.categoryId}," +
                    "${transaction.date.time},${transaction.type}"
        })
    }

    // Existing method for restore (assumed to read transactions from internal storage)
    fun readTransactionsFromFile(): List<Transaction> {
        val file = File(context.filesDir, "transactions_backup.txt")
        if (!file.exists()) return emptyList()
        return file.readText().split("\n").filter { it.isNotBlank() }.map { line ->
            val parts = line.split(",")
            Transaction(
                id = parts[0].toInt(),
                title = parts[1],
                amount = parts[2].toDouble(),
                categoryId = parts[3].toInt(),
                date = Date(parts[4].toLong()),
                type = parts[5]
            )
        }
    }

    // New method for exporting transactions as JSON
    fun exportTransactionsToJson(transactions: List<Transaction>) {
        val jsonArray = JSONArray()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        transactions.forEach { transaction ->
            val jsonObject = JSONObject().apply {
                put("id", transaction.id)
                put("title", transaction.title)
                put("amount", transaction.amount)
                put("categoryId", transaction.categoryId)
                put("date", dateFormat.format(transaction.date))
                put("type", transaction.type)
            }
            jsonArray.put(jsonObject)
        }

        val jsonString = jsonArray.toString(2) // Pretty print with indentation
        val fileName = "SmartSpendy_Transactions_${System.currentTimeMillis()}.json"
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        file.writeText(jsonString)
    }
}