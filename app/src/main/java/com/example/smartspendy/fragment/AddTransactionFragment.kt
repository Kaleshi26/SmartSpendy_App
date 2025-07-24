package com.example.smartspendy.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.smartspendy.R
import com.example.smartspendy.databinding.FragmentAddTransactionBinding
import com.example.smartspendy.model.Category
import com.example.smartspendy.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by activityViewModels()
    private var selectedDate: Date? = null
    private var transactionId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerMenu()
        setupCategories()
        setupDatePicker()
        setupTransactionType()
        setupSaveButton()

        transactionId = arguments?.getInt("transactionId") ?: -1
        if (transactionId != -1) {
            // Edit mode
            viewModel.transactions.value?.find { it.id == transactionId }?.let { transaction ->
                binding.etTitle.setText(transaction.title)
                binding.etAmount.setText(transaction.amount.toString())
                binding.spinnerCategory.setSelection(getCategoryPosition(transaction.categoryId))
                binding.spinnerType.setSelection(if (transaction.type == "Income") 0 else 1)
                selectedDate = transaction.date
                binding.etDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(transaction.date))
            }
        }
    }

    private fun setupDrawerMenu() {
        val menuIcon = binding.root.findViewById<View>(R.id.menu_icon)
        menuIcon?.setOnClickListener {
            val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    private fun setupCategories() {
        val categories = listOf(
            Category(1, "Food"),
            Category(2, "Transport"),
            Category(3, "Bills"),
            Category(4, "Entertainment"),
            Category(5, "Other")
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories.map { it.name })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                selectedDate = selectedCalendar.time
                binding.etDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate!!))
            }, year, month, day).show()
        }
    }

    private fun setupTransactionType() {
        val types = listOf("Income", "Expense")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val amount = binding.etAmount.text.toString().toDoubleOrNull()
            val categoryPosition = binding.spinnerCategory.selectedItemPosition
            val type = binding.spinnerType.selectedItem.toString()

            if (title.isEmpty() || amount == null || selectedDate == null) {
                binding.addTransactionTextView.text = "Please fill all fields"
                return@setOnClickListener
            }

            val categoryId = categoryPosition + 1 // Categories start from ID 1

            if (transactionId != -1) {
                // Update existing transaction
                viewModel.updateTransaction(transactionId, title, amount, categoryId, selectedDate!!, type)
            } else {
                // Add new transaction
                viewModel.addTransaction(title, amount, categoryId, selectedDate!!, type)
            }

            findNavController().navigateUp()
        }
    }

    private fun getCategoryPosition(categoryId: Int): Int {
        return categoryId - 1 // Categories start from ID 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}