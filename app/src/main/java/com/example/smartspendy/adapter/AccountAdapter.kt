package com.example.smartspendy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartspendy.R
import com.example.smartspendy.databinding.ItemAccountBinding
import com.example.smartspendy.fragment.Account

class AccountAdapter :
    ListAdapter<Account, AccountAdapter.ViewHolder>(AccountDiffCallback()) {

    class ViewHolder(private val binding: ItemAccountBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Account) {
            binding.tvAccountName.text = item.name
            binding.tvBalance.text = "Balance: Rs ${item.balance}"
            binding.tvTransactionCount.text = "Transactions: ${item.transactionCount}"
            binding.tvLastTransaction.text = "Last Transaction: ${item.lastTransactionDate}"

            // Set account icon
            val iconResId = when (item.name.lowercase()) {
                "card" -> R.drawable.ic_card
                "cash" -> R.drawable.ic_cash
                "savings" -> R.drawable.ic_savings
                else -> R.drawable.ic_default
            }
            binding.ivAccountIcon.setImageResource(iconResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class AccountDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Account>() {
    override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean {
        return oldItem == newItem
    }
}