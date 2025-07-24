package com.example.smartspendy.fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartspendy.R
import com.example.smartspendy.adapter.TransactionAdapter
import com.example.smartspendy.databinding.FragmentRecordsBinding
import com.example.smartspendy.viewmodel.TransactionViewModel

class RecordsFragment : Fragment() {

    private var _binding: FragmentRecordsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by activityViewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupDrawerMenu()

        // Floating Action Button animation on appearance
        val fabAnimator = ObjectAnimator.ofFloat(binding.fabAdd, "translationY", 300f, 0f)
        fabAnimator.duration = 500
        fabAnimator.interpolator = AccelerateDecelerateInterpolator()
        fabAnimator.start()

        // FAB click action
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_RecordsFragment_to_AddTransactionFragment)
        }

        // Send daily reminder (this can be triggered by a WorkManager in a real app)
        viewModel.sendDailyReminder()
    }

    private fun setupDrawerMenu() {
        val menuIcon = binding.root.findViewById<View>(R.id.menu_icon)
        menuIcon?.setOnClickListener {
            val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(
            onEditClick = { transaction ->
                val bundle = Bundle().apply {
                    putInt("transactionId", transaction.id)
                }
                findNavController().navigate(R.id.action_RecordsFragment_to_AddTransactionFragment, bundle)
            },
            onDeleteClick = { transaction ->
                viewModel.deleteTransaction(transaction.id)
            }
        )
        binding.rvTransactions.layoutManager = LinearLayoutManager(context)
        binding.rvTransactions.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)
            val totalExpense = viewModel.getTotalExpense()
            updateBudgetWarning(totalExpense)
        }

        viewModel.budget.observe(viewLifecycleOwner) { budget ->
            val progress = viewModel.getBudgetProgress()
            binding.budgetProgress.progress = progress
            binding.tvBudgetStatus.text = "Budget: ${viewModel.currency.value ?: "Rs"} $budget"
            updateBudgetWarning(viewModel.getTotalExpense())
        }
    }

    private fun updateBudgetWarning(totalExpense: Double) {
        val budget = viewModel.budget.value ?: 0.0
        val currency = viewModel.currency.value ?: "Rs"
        binding.tvBudgetWarning.visibility = View.VISIBLE
        when {
            budget <= 0.0 -> {
                binding.tvBudgetWarning.text = "No budget set."
                binding.tvBudgetWarning.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
            totalExpense >= budget -> {
                binding.tvBudgetWarning.text = "Warning: You've exceeded your budget of $currency $budget!"
                binding.tvBudgetWarning.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
            totalExpense >= budget * 0.9 -> {
                binding.tvBudgetWarning.text = "Warning: You're nearing your budget of $currency $budget!"
                binding.tvBudgetWarning.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
            else -> {
                binding.tvBudgetWarning.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}