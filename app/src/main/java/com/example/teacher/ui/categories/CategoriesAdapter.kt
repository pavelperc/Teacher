package com.example.teacher.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.teacher.databinding.ItemCategoryBinding

class CategoriesAdapter : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    var categories: List<CategoriesViewModel.CategoryWithStats> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryWithStats = categories[position]
        holder.binding.categoryWithStats = categoryWithStats
        holder.binding.executePendingBindings()
        holder.binding.cardView.setOnClickListener {
            val action = CategoriesFragmentDirections.actionOpenCategory(categoryWithStats.id)
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}