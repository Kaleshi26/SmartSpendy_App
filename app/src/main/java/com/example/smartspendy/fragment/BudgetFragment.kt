package com.example.smartspendy.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartspendy.R
import com.example.smartspendy.adapter.BudgetAdapter
import com.example.smartspendy.databinding.FragmentBudgetBinding
import com.example.smartspendy.model.BudgetItem
import com.example.smartspendy.viewmodel.TransactionViewModel

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by activityViewModels()
    private lateinit var budgetAdapter: BudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerMenu()
        setupCurrencySpinner()
        setupRecyclerView()
        populateBudgetItems()
        observeViewModel()
        setupSaveButton()
    }

    private fun setupDrawerMenu() {
        val menuIcon = binding.root.findViewById<View>(R.id.menu_icon)
        menuIcon?.setOnClickListener {
            val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    private fun setupCurrencySpinner() {
        val currencies = listOf("Rs", "US Dollar ($)", "Euro (€)", "British Pound (£)", "Japanese Yen (¥)", "Indian Rupee (₹)", "Australian Dollar (A$)", "Canadian Dollar (C$)")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCurrency.adapter = adapter
        binding.spinnerCurrency.setSelection(currencies.indexOf(viewModel.currency.value ?: "Rs"))
    }

    private fun setupRecyclerView() {
        budgetAdapter = BudgetAdapter()
        binding.rvBudgets.layoutManager = LinearLayoutManager(context)
        binding.rvBudgets.adapter = budgetAdapter
    }

    private fun populateBudgetItems() {
        val budgetItems = listOf(
            // Monthly Budgets
            BudgetItem(
                categoryId = 1, // Food (Eating out)
                startDate = "01/04/2025",
                endDate = "01/05/2025",
                spentAmount = 36000.00,
                totalAmount = 12000.00,
                residualAmount = 6540.00,
                progress = 36,
                period = "Monthly"
            ),
            BudgetItem(
                categoryId = 4, // Entertainment
                startDate = "07/04/2025",
                endDate = "13/04/2025",
                spentAmount = 85000.00,
                totalAmount = 30500.00,
                residualAmount = 2250.00,
                progress = 27,
                period = "Monthly"
            )
        )
        budgetAdapter.submitList(budgetItems)

        // Update header totals
        val monthlyTotalSpent = budgetItems.filter { it.period == "Monthly" }.sumOf { it.spentAmount }
        val monthlyTotalBudget = budgetItems.filter { it.period == "Monthly" }.sumOf { it.totalAmount }
        binding.tvMonthlyBudgets.text = "Monthly budgets\nRs$monthlyTotalSpent / Rs$monthlyTotalBudget"
    }

    private fun setupSaveButton() {
        binding.btnSaveBudget.setOnClickListener {
            val budget = binding.etBudgetAmount.text.toString().toDoubleOrNull() ?: 0.0
            val currency = binding.spinnerCurrency.selectedItem.toString()
            viewModel.setBudget(budget)
            viewModel.setCurrency(currency)
        }
    }

    private fun observeViewModel() {
        viewModel.budget.observe(viewLifecycleOwner) { budget ->
            binding.etBudgetAmount.setText(budget.toString())
            binding.budgetTextView.text = "Your Monthly Budget: ${viewModel.currency.value ?: "Rs"} $budget"
        }

        viewModel.currency.observe(viewLifecycleOwner) { currency ->
            binding.budgetTextView.text = "Your Monthly Budget: $currency ${viewModel.budget.value ?: 0.0}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}