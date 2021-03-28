package com.example.teacher.ui.questions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teacher.R
import com.example.teacher.databinding.FragmentQuestionsBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class QuestionsFragment : Fragment() {

    private val viewModel by viewModels<QuestionsViewModel>()

    private var _binding: FragmentQuestionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val categoryId = QuestionsFragmentArgs.fromBundle(requireArguments()).categoryId
        viewModel.setCategoryId(categoryId)

        _binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // don't use data binding here, because it supports StateFlow only in alpha version

        // toolbar.setupWithNavController() breaks the toolbar text and icon color, so don't use it
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        val questionsAdapter = QuestionsAdapter()
        binding.recyclerViewQuestions.apply {
            adapter = questionsAdapter
            layoutManager = LinearLayoutManager(context)
        }

        lifecycleScope.launch {
            viewModel.questionsWithStats.collect { questionsWithStats ->
                questionsAdapter.questions = questionsWithStats
            }
        }

        lifecycleScope.launch {
            viewModel.learnedCount.collect { learnedCount ->
                binding.textViewLearned.text = getString(
                    R.string.category_learned_stats,
                    learnedCount, viewModel.questionsCount
                )
            }
        }

        lifecycleScope.launch {
            viewModel.progressPercent.collect { percent ->
                binding.textViewPercent.text = "$percent%"
                binding.progressBar.progress = percent
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}