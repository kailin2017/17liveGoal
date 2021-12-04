package com.kailin.goal.widget.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.kailin.goal.app.MyApplication
import java.util.*
import kotlin.collections.ArrayList

abstract class MyRecyclerAdapter<V : ViewDataBinding, D>(private val onItemClick: View.OnClickListener? = null) :
    RecyclerView.Adapter<ViewHolder<V>>() {

    protected abstract val viewLayoutRes: Int
    protected val data: MutableList<D> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<V> {
        return ViewHolder(createBinding(parent, viewType)).apply {
            binding.root.setOnClickListener(onItemClick)
        }
    }

    protected open fun createBinding(parent: ViewGroup, viewType: Int): V {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), viewLayoutRes, parent, false
        )
    }

    override fun getItemCount() = data.size

    fun clearData() {
        updateData(ArrayList())
    }

    fun updateData(newData: MutableList<D>) {
        calculateDiff(data, newData)
    }

    fun appendData(giveData: MutableList<D>) {
        val newData: MutableList<D> = ArrayList()
        newData.addAll(data)
        newData.addAll(giveData)
        updateData(newData)
    }

    fun <D> calculateDiff(mainData: MutableList<D>, newData: MutableList<D>) {
        val result = DiffUtil.calculateDiff(MyDiffCallback(mainData, newData))
        mainData.clear()
        mainData.addAll(newData)
        result.dispatchUpdatesTo(this@MyRecyclerAdapter)
    }

    fun getContext(): Context {
        return MyApplication.instance
    }
}

class ViewHolder<V : ViewDataBinding>(val binding: V) : RecyclerView.ViewHolder(binding.root)