package com.example.teacher.ui.quiz

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.teacher.R
import com.example.teacher.databinding.FragmentQuizBinding
import kotlinx.coroutines.flow.collect

class QuizFragment : Fragment() {

    private val viewModel by viewModels<QuizViewModel>()

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val (categoryId, isLanguageSwapped) = QuizFragmentArgs.fromBundle(requireArguments())
        viewModel.initWithCategoryAndSettings(categoryId, isLanguageSwapped)

        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.isVerticalOrientation =
            resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener {
            exitQuiz()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.headerViewState.collect { headerViewState ->
                binding.headerViewState = headerViewState
                binding.executePendingBindings()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.quizViewState.collect { viewState ->
                when (viewState) {
                    is QuizViewModel.QuizViewState.Finish -> {
                        findNavController().navigateUp()
                        return@collect
                    }
                    is QuizViewModel.QuizViewState.Empty -> {
                        // all is hiding with data binding
                    }
                    is QuizViewModel.QuizViewState.Answered -> {
                        if (viewState.isCorrect) {
                            binding.textViewVerdict.setText(R.string.quiz_correct)
                            binding.textViewVerdict.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.dark_green)
                            )
                        } else {
                            binding.textViewVerdict.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.red)
                            )
                            binding.textViewVerdict.setText(R.string.quiz_wrong)
                        }
                        binding.textViewAnswered.text = viewState.answer
                        binding.textViewQuestion.text = viewState.question
                    }
                    is QuizViewModel.QuizViewState.Question -> {
                        binding.textViewQuestion.text = viewState.question
                        binding.textViewAnswer1.text = viewState.answer1
                        binding.textViewAnswer2.text = viewState.answer2
                        binding.textViewAnswer3.text = viewState.answer3
                        binding.textViewAnswer4.text = viewState.answer4
                    }
                }
                binding.quizViewState = viewState
                binding.executePendingBindings()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            exitQuiz()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun exitQuiz() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.alert_stop_quiz)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                findNavController().navigateUp()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}