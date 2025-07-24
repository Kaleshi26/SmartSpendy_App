package com.example.smartspendy.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.smartspendy.databinding.FragmentReportsBinding
import com.example.smartspendy.viewmodel.TransactionViewModel

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.categorySummary.observe(viewLifecycleOwner) { categories ->
            val currency = viewModel.currency.value ?: "Rs"
            val summary = categories.joinToString("\n") { category ->
                "${category.name}: $currency ${category.totalAmount}"
            }
            binding.tvCategorySummary.text = summary.ifEmpty { "No transactions yet" }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
