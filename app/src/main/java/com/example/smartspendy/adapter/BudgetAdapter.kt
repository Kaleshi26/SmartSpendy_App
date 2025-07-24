package com.example.smartspendy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartspendy.R
import com.example.smartspendy.databinding.ItemBudgetBinding
import com.example.smartspendy.model.BudgetItem
import com.example.smartspendy.model.Category // Import the Category class

class BudgetAdapter : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    private val budgetItems = mutableListOf<BudgetItem>()
    private val categories = listOf(
        Category(1, "Food"),
        Category(2, "Transport"),
        Category(3, "Bills"),
        Category(4, "Entertainment"),
        Category(5, "Other")
    )

    fun submitList(items: List<BudgetItem>) {
        budgetItems.clear()
        budgetItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(budgetItems[position])
    }

    override fun getItemCount(): Int = budgetItems.size

    inner class BudgetViewHolder(private val binding: ItemBudgetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(budgetItem: BudgetItem) {
            val categoryName = categories.find { it.id == budgetItem.categoryId }?.name ?: "Unknown"
            binding.tvCategory.text = categoryName
            binding.tvProgressPercentage.text = "${budgetItem.progress}%"
            binding.tvStartDate.text = budgetItem.startDate
            binding.tvEndDate.text = budgetItem.endDate
            binding.tvSpentAmount.text = "Rs${budgetItem.spentAmount}"
            binding.tvTotalAmount.text = "Rs${budgetItem.totalAmount}"
            binding.tvResidualAmount.text = "Residual amount: Rs${budgetItem.residualAmount}"
            binding.budgetProgress.progress = budgetItem.progress

            // Set category icon
            val iconResId = when (categoryName.lowercase()) {
                "food" -> R.drawable.ic_eating_out
                "transport" -> R.drawable.ic_transportation
                "bills" -> R.drawable.ic_shopping
                "entertainment" -> R.drawable.ic_entertainment
                "other" -> R.drawable.ic_gifts
                else -> R.drawable.ic_default
            }
            binding.ivCategoryIcon.setImageResource(iconResId)
        }
    }
}