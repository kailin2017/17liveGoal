package com.kailin.goal.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.kailin.goal.R
import com.kailin.goal.api.github.Repo
import com.kailin.goal.databinding.ItemMainBinding
import com.kailin.goal.databinding.ItemMainLoadingBinding
import com.kailin.goal.utils.GlideHelper
import com.kailin.goal.widget.recyclerView.MyRecyclerAdapter
import com.kailin.goal.widget.recyclerView.ViewHolder

class MainAdapter : MyRecyclerAdapter<ViewDataBinding, Repo>() {

    override val viewLayoutRes: Int = R.layout.item_main
    private val glideHelper = GlideHelper.instance
    private var loadingCallback: ((Boolean) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder<ViewDataBinding>, position: Int) {
        when (holder.binding) {
            is ItemMainBinding -> {
                val gitDataItem = data[position]
                holder.binding.itemData = gitDataItem
                addChop(holder.binding.topics, gitDataItem.topics)
                glideHelper.load(holder.binding.ownerAvatar, gitDataItem.owner.avatar_url)
            }
            is ItemMainLoadingBinding -> {
                loadingCallback = {
                    if (it) {
                        holder.binding.progress.visibility = View.VISIBLE
                        holder.binding.msg.visibility = View.GONE
                    } else {
                        holder.binding.progress.visibility = View.GONE
                        holder.binding.msg.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun addChop(chipGroup: ChipGroup, topics: List<String>) {
        topics.forEach {
            val context = chipGroup.context
            val chip = Chip(context)
            with(chip) {
                setChipBackgroundColorResource(R.color.chipBG)
                setTextColor(Color.WHITE)
                text = it
            }
            chipGroup.addView(chip)
        }
    }

    fun showLoading() {
        loadingCallback?.let { it(true) }
    }

    fun hideLoading() {
        loadingCallback?.let { it(false) }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        val viewLayoutRes = when (viewType) {
            viewType_item -> {
                R.layout.item_main
            }
            else -> {
                R.layout.item_main_loading
            }
        }
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), viewLayoutRes, parent, false
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < data.size) {
            viewType_item
        } else {
            viewType_last
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    companion object {
        private const val viewType_item = 0
        private const val viewType_last = 1
    }
}