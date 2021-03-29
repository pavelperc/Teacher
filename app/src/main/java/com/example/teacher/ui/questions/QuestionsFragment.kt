package com.example.teacher.ui.questions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teacher.R
import com.example.teacher.databinding.FragmentQuestionsBinding
import kotlinx.coroutines.flow.collect

class QuestionsFragment : Fragment() {

    private val viewModel by viewModels<QuestionsViewModel>()

    private var _binding: FragmentQuestionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val categoryId = QuestionsFragmentArgs.fromBundle(requireArguments()).categoryId
        viewModel.initWithCategoryId(categoryId)

        _binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        val questionsAdapter = QuestionsAdapter()
        binding.recyclerViewQuestions.apply {
            adapter = questionsAdapter
            layoutManager = LinearLayoutManager(context)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.questionViewStates.collect { questionViewStates ->
                questionsAdapter.questionViewStates = questionViewStates
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.categoryViewState.collect {
                binding.viewState = it
                binding.executePendingBindings()
            }
        }

        binding.buttonStart.setOnClickListener {
            if (!viewModel.hasNotLearnedQuestions) {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.alert_reset_questions_title)
                    .setMessage(R.string.alert_reset_questions_message)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        lifecycleScope.launchWhenStarted {
                            viewModel.resetLearnedQuestions()
                            startQuiz()
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()

            } else {
                startQuiz()
            }
        }
    }

    private fun startQuiz() {
        val action = QuestionsFragmentDirections
            .actionStartQuiz(viewModel.categoryId, viewModel.isLanguageSwapped)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}