package com.example.teacher.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teacher.databinding.ItemCategoryBinding

class CategoriesAdapter(
    private var categories: List<CategoriesViewModel.CategoryWithStats>,
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.categoryWithStats = categories[position]
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}