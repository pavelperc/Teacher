package com.example.teacher.ui.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.teacher.databinding.FragmentCategoriesBinding
import com.example.teacher.utils.dip
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.max

class CategoriesFragment : Fragment() {

    private val viewModel by viewModels<CategoriesViewModel>()

    private val categoriesAdapter = CategoriesAdapter()

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            viewModel.categoryDescriptions.collect { categoryDescriptions ->
                categoriesAdapter.categoryDescriptions = categoryDescriptions
            }
        }

        binding.recyclerView.apply {
            post { // wait until width is evaluated
                val columnsCount = max(2, width / dip(200))
                layoutManager = GridLayoutManager(context, columnsCount)
                adapter = categoriesAdapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}