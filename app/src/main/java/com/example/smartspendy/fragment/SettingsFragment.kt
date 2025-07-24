package com.example.smartspendy.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.smartspendy.R
import com.example.smartspendy.databinding.FragmentSettingsBinding
import com.example.smartspendy.viewmodel.TransactionViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.budget.observe(viewLifecycleOwner) { budget ->
            binding.etBudget.setText(budget.toString())
        }

        viewModel.currency.observe(viewLifecycleOwner) { currency ->
            binding.etCurrency.setText(currency)
        }

        binding.btnSaveSettings.setOnClickListener {
            saveSettings()
        }

        binding.btnExport.setOnClickListener {
            viewModel.exportTransactions()
            Toast.makeText(context, R.string.data_exported, Toast.LENGTH_SHORT).show()
        }

        binding.btnImport.setOnClickListener {
            viewModel.importTransactions()
            Toast.makeText(context, R.string.data_imported, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSettings() {
        val budgetStr = binding.etBudget.text.toString().trim()
        val currency = binding.etCurrency.text.toString().trim()

        val budget = budgetStr.toDoubleOrNull() ?: 0.0
        if (budget < 0) {
            binding.tilBudget.error = "Budget cannot be negative"
            return
        }

        viewModel.setBudget(budget)
        viewModel.setCurrency(currency.ifEmpty { "Rs" })
        Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}