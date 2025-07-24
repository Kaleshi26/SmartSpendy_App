package com.example.smartspendy.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartspendy.R
import com.example.smartspendy.adapter.AccountAdapter
import com.example.smartspendy.databinding.FragmentAccountsBinding
import com.example.smartspendy.viewmodel.TransactionViewModel

class AccountsFragment : Fragment() {

    private var _binding: FragmentAccountsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by activityViewModels()
    private lateinit var adapter: AccountAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerMenu()
        setupRecyclerView()
        setupAddAccountButton()
    }

    private fun setupDrawerMenu() {
        val menuIcon = binding.root.findViewById<View>(R.id.menu_icon)
        menuIcon?.setOnClickListener {
            val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    private fun setupRecyclerView() {
        adapter = AccountAdapter()
        binding.rvAccounts.layoutManager = LinearLayoutManager(context)
        binding.rvAccounts.adapter = adapter

        val accounts = listOf(
            Account("Card", 1500.50, 12, "20/04/2025"),
            Account("Cash", 450.25, 8, "18/04/2025"),
            Account("Savings", 3200.75, 5, "15/04/2025")
        )
        adapter.submitList(accounts)

        // Calculate and display total balance
        val totalBalance = accounts.sumOf { it.balance }
        binding.tvTotalBalance.text = "Total Balance: Rs $totalBalance"
    }

    private fun setupAddAccountButton() {
        binding.btnAddAccount.setOnClickListener {
            // Placeholder for adding a new account
            // You can navigate to an "Add Account" fragment or show a dialog here
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class Account(
    val name: String,
    val balance: Double,
    val transactionCount: Int = 0,
    val lastTransactionDate: String = "N/A"
)