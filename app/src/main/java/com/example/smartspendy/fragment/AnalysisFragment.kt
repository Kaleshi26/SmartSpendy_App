package com.example.smartspendy.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartspendy.R
import com.example.smartspendy.databinding.FragmentAnalysisBinding
import com.example.smartspendy.databinding.ItemCategoryExpenseBinding
import com.example.smartspendy.model.Category
import com.example.smartspendy.viewmodel.TransactionViewModel
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieData

class AnalysisFragment : Fragment() {

    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by activityViewModels()
    private lateinit var adapter: CategoryExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerMenu()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupDrawerMenu() {
        val menuIcon = binding.root.findViewById<View>(R.id.menu_icon)
        menuIcon?.setOnClickListener {
            val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoryExpenseAdapter()
        binding.rvCategoryExpenses.layoutManager = LinearLayoutManager(context)
        binding.rvCategoryExpenses.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            val expensesByCategory = viewModel.getExpensesByCategory()
            val categories = listOf(
                Category(1, "Food"),
                Category(2, "Transport"),
                Category(3, "Bills"),
                Category(4, "Entertainment"),
                Category(5, "Other") // Added the "Other" category
            )
            val categoryExpenses = categories.map { category ->
                val amount = expensesByCategory[category.id] ?: 0.0
                CategoryExpense(category.name, amount)
            }

            adapter.submitList(categoryExpenses)
            updatePieChart(categoryExpenses)

            // Update total expense, income, and total row
            val totalExpense = viewModel.getTotalExpense()
            val totalIncome = viewModel.getTotalIncome()
            binding.tvTotalExpense.text = "Rs$totalExpense"
            binding.tvTotalIncome.text = "Rs$totalIncome"
            binding.tvTotalAmount.text = "Rs $totalExpense"
        }
    }

    private fun updatePieChart(categoryExpenses: List<CategoryExpense>) {
        val entries = categoryExpenses.map {
            PieEntry(it.amount.toFloat(), it.categoryName)
        }

        // Create a PieDataSet
        val dataSet = PieDataSet(entries, "Category Summary").apply {
            colors = listOf(
                ContextCompat.getColor(requireContext(), R.color.category_food),
                ContextCompat.getColor(requireContext(), R.color.category_transport),
                ContextCompat.getColor(requireContext(), R.color.category_bills),
                ContextCompat.getColor(requireContext(), R.color.category_entertainment),
                ContextCompat.getColor(requireContext(), R.color.category_other) // Added color for "Other"
            )
            valueTextSize = 12f
        }

        val pieData = PieData(dataSet)
        binding.pieChart.apply {
            data = pieData
            description.isEnabled = false
            isDrawHoleEnabled = true
            setEntryLabelTextSize(12f)
            centerText = "Expenses"
            animateY(1000)
            invalidate() // Refresh the chart
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class CategoryExpense(val categoryName: String, val amount: Double)

// Adapter for category expenses
class CategoryExpenseAdapter :
    androidx.recyclerview.widget.ListAdapter<CategoryExpense, CategoryExpenseAdapter.ViewHolder>(
        CategoryExpenseDiffCallback()
    ) {

    private var totalAmount: Double = 0.0

    override fun submitList(list: List<CategoryExpense>?) {
        super.submitList(list)
        totalAmount = list?.sumOf { it.amount } ?: 0.0
    }

    class ViewHolder(private val binding: ItemCategoryExpenseBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryExpense, totalAmount: Double) {
            binding.tvCategoryName.text = item.categoryName
            binding.tvAmount.text = "Rs ${item.amount}"
            val percentage = if (totalAmount > 0) ((item.amount / totalAmount) * 100).toInt() else 0
            binding.tvPercentage.text = "$percentage%"

            // Assign colors based on category name
            val context = binding.root.context
            binding.colorIndicator.backgroundTintList = when (item.categoryName) {
                "Food" -> ContextCompat.getColorStateList(context, R.color.category_food)
                "Transport" -> ContextCompat.getColorStateList(context, R.color.category_transport)
                "Bills" -> ContextCompat.getColorStateList(context, R.color.category_bills)
                "Entertainment" -> ContextCompat.getColorStateList(context, R.color.category_entertainment)
                "Other" -> ContextCompat.getColorStateList(context, R.color.category_other) // Color for "Other"
                else -> ContextCompat.getColorStateList(context, R.color.category_default)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryExpenseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), totalAmount)
    }
}

class CategoryExpenseDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<CategoryExpense>() {
    override fun areItemsTheSame(oldItem: CategoryExpense, newItem: CategoryExpense): Boolean {
        return oldItem.categoryName == newItem.categoryName
    }

    override fun areContentsTheSame(oldItem: CategoryExpense, newItem: CategoryExpense): Boolean {
        return oldItem == newItem
    }
}
