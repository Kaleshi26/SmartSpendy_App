package com.example.smartspendy.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.smartspendy.R
import com.example.smartspendy.databinding.FragmentBackupBinding
import com.example.smartspendy.utils.FileHelper
import com.example.smartspendy.utils.SharedPrefsHelper
import com.example.smartspendy.viewmodel.TransactionViewModel

class BackupFragment : Fragment() {

    private var _binding: FragmentBackupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by activityViewModels()
    private lateinit var fileHelper: FileHelper
    private lateinit var sharedPrefsHelper: SharedPrefsHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBackupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerMenu()
        setupBackupRestore()

        fileHelper = FileHelper(requireContext())
        sharedPrefsHelper = SharedPrefsHelper(requireContext())
    }

    private fun setupDrawerMenu() {
        val menuIcon = binding.root.findViewById<View>(R.id.menu_icon)
        menuIcon?.setOnClickListener {
            val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    private fun setupBackupRestore() {
        binding.btnBackup.setOnClickListener {
            val transactions = viewModel.transactions.value ?: emptyList()
            fileHelper.saveTransactionsToFile(transactions)
            Toast.makeText(context, "Backup successful", Toast.LENGTH_SHORT).show()
        }

        binding.btnRestore.setOnClickListener {
            val transactions = fileHelper.readTransactionsFromFile()
            sharedPrefsHelper.saveTransactions(transactions)
            viewModel.importTransactions() // ✅ This safely updates LiveData
            Toast.makeText(context, "Restore successful", Toast.LENGTH_SHORT).show()
        }

        binding.btnExport.setOnClickListener {
            val transactions = viewModel.transactions.value ?: emptyList()
            fileHelper.exportTransactionsToJson(transactions)
            Toast.makeText(context, "Export successful", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}