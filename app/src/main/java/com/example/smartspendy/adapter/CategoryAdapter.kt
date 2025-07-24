package com.example.smartspendy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartspendy.R
import com.example.smartspendy.databinding.ItemCategoryBinding
import com.example.smartspendy.model.Category

class CategoryAdapter : ListAdapter<Category, CategoryAdapter.ViewHolder>(CategoryDiffCallback()) {

    class ViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            binding.tvCategoryName.text = item.name

            // Set category icon
            val iconResId = when (item.name.lowercase()) {
                "supermarket" -> R.drawable.ic_supermarket
                "clothing" -> R.drawable.ic_clothing
                "house" -> R.drawable.ic_house
                "entertainment" -> R.drawable.ic_entertainment
                "transport" -> R.drawable.ic_transport
                "gifts" -> R.drawable.ic_gifts
                "travel" -> R.drawable.ic_travel
                "education" -> R.drawable.ic_education
                "food" -> R.drawable.ic_food
                else -> R.drawable.ic_default
            }
            binding.ivCategoryIcon.setImageResource(iconResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean = oldItem == newItem
}