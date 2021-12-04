package com.kailin.goal.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.kailin.goal.app.BaseFragment
import com.kailin.goal.databinding.FragmentMainBinding
import androidx.recyclerview.widget.LinearLayoutManager

class MainFragment : BaseFragment<MainViewModel, FragmentMainBinding>() {

    override val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: MainAdapter
    private var isLoading: Boolean = false

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMainBinding.inflate(inflater, container, false)

    override fun initView() {
        adapter = MainAdapter()
        viewDataBinding?.recyclerView?.let {
            it.adapter = adapter
            it.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayout.VERTICAL))
            it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    if (isLoading) {
                        return
                    }
                    if (linearLayoutManager?.findLastCompletelyVisibleItemPosition()!! > adapter.itemCount - 5) {
                        viewModel.searchRepoLoadNext()
                    }
                }
            })
        }
        viewDataBinding?.searchEditText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchRepo()
                true
            } else {
                false
            }
        }
        viewDataBinding?.searchEditText?.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                searchRepo()
                true
            } else {
                false
            }
        }
        viewModel.repoData.observe(this) { result ->
            result?.let {
                when (it.first) {
                    MainViewModel.state_searchData -> {
                        adapter.updateData(it.second)
                        viewDataBinding?.recyclerView?.scrollToPosition(0)
                    }
                    MainViewModel.state_loadNextData -> {
                        adapter.appendData(it.second)
                    }
                }
            }
        }
        searchRepo()
    }

    private fun searchRepo() {
        val searchKeyword = viewDataBinding?.searchEditText?.text.toString()
        viewModel.searchRepo(searchKeyword)
        adapter.clearData()
        hideKeyboard()
    }

    override fun onLoading() {
        isLoading = true
        adapter.showLoading()
    }

    override fun onLoaded() {
        isLoading = false
        adapter.hideLoading()
    }
}


