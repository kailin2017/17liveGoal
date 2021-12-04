package com.kailin.goal.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.kailin.goal.widget.DialogHelper

abstract class BaseFragment<M : BaseViewModel, V : ViewDataBinding> : Fragment() {

    protected var viewDataBinding: V? = null
    protected abstract val viewModel: M

    protected val dialogHelper = DialogHelper.instance

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = initBinding(inflater, container, savedInstanceState)
        return viewDataBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            msgText.observe(viewLifecycleOwner, {
                dialogHelper.msgDialog(requireContext(), msg = it)
            })
            loading.observe(viewLifecycleOwner, {
                if (it) {
                    onLoading()
                } else {
                    onLoaded()
                }
            })
        }
        initView()
    }

    protected abstract fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): V

    protected abstract fun initView()

    protected open fun onLoading() {}

    protected open fun onLoaded() {}

    protected fun setToolbar(toolbar: Toolbar) {
        requireActivity().apply {
            if (this is AppCompatActivity) {
                setSupportActionBar(toolbar)
                setHasOptionsMenu(true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelAllJobs()
        viewDataBinding = null
    }

    fun hideKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}