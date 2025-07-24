package com.example.smartspendy.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartspendy.R
import com.example.smartspendy.databinding.FragmentCategoriesBinding
import com.example.smartspendy.model.Category
import com.example.smartspendy.adapter.CategoryAdapter

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDrawerMenu()
        setupRecyclerView()

        binding.btnBackup.setOnClickListener {
            findNavController().navigate(R.id.action_CategoriesFragment_to_BackupFragment)
        }
    }

    private fun setupDrawerMenu() {
        val menuIcon = binding.root.findViewById<View>(R.id.menu_icon)
        menuIcon?.setOnClickListener {
            val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter()
        binding.rvCategories.layoutManager = LinearLayoutManager(context)
        binding.rvCategories.adapter = adapter

        val categories = listOf(
            Category(1, "Supermarket"),
            Category(2, "Clothing"),
            Category(3, "House"),
            Category(4, "Entertainment"),
            Category(5, "Transport"),
            Category(6, "Gifts"),
            Category(7, "Travel"),
            Category(8, "Education"),
            Category(9, "Food")
        )
        adapter.submitList(categories)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}