package com.example.smartspendy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartspendy.R
import com.example.smartspendy.databinding.ItemTransactionBinding
import com.example.smartspendy.model.Category
import com.example.smartspendy.model.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private val onEditClick: (Transaction) -> Unit,
    private val onDeleteClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    private val categories = listOf(
        Category(1, "Food"),
        Category(2, "Transport"),
        Category(3, "Bills"),
        Category(4, "Entertainment"),
        Category(5, "Other")
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding,
        private val onEditClick: (Transaction) -> Unit,
        private val onDeleteClick: (Transaction) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.tvTitle.text = transaction.title
            binding.tvAmount.text = "${transaction.type}: Rs ${transaction.amount}"
            val categoryName = categories.find { it.id == transaction.categoryId }?.name ?: "Unknown"
            binding.tvCategory.text = categoryName
            binding.tvDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(transaction.date)

            // Set category icon based on category name
            val iconResId = when (categoryName.lowercase()) {
                "food" -> R.drawable.ic_eating_out // Maps to "Eating out" or "Bar" icon
                "transport" -> R.drawable.ic_transportation // Maps to "Transportation" icon
                "bills" -> R.drawable.ic_shopping // Maps to "Shopping, Home" or "Technology" icon
                "entertainment" -> R.drawable.ic_entertainment // Maps to "Entertainment" icon
                "other" -> R.drawable.ic_gifts // Maps to "Gifts", "Fuel", "Salary" icon
                else -> R.drawable.ic_default // Fallback icon
            }
            binding.ivCategoryIcon.setImageResource(iconResId)

            // Set amount text color based on type (income or expense)
            val amountColor = if (transaction.amount >= 0) {
                binding.root.context.getColor(android.R.color.holo_green_dark)
            } else {
                binding.root.context.getColor(android.R.color.holo_red_dark)
            }
            binding.tvAmount.setTextColor(amountColor)

            binding.btnEdit.setOnClickListener { onEditClick(transaction) }
            binding.btnDelete.setOnClickListener { onDeleteClick(transaction) }
        }
    }
}

class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }
}