package com.example.teacher.ui.questions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.teacher.databinding.ItemQuestionBinding

class QuestionsAdapter : RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {

    var questionViewStates: List<QuestionsViewModel.QuestionViewState> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemQuestionBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val questionViewState = questionViewStates[position]
        holder.binding.questionViewState = questionViewState
        holder.binding.executePendingBindings()
        holder.binding.divider.isVisible = position != questionViewStates.size - 1
    }

    override fun getItemCount(): Int {
        return questionViewStates.size
    }
}