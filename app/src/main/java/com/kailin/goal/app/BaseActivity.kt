package com.kailin.goal.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import com.kailin.goal.widget.DialogHelper

abstract class BaseActivity<M : BaseViewModel, V : ViewDataBinding> : AppCompatActivity() {

    protected lateinit var viewDataBinding: V
    protected abstract val viewModel: M

    protected val dialogHelper = DialogHelper.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding = initBinding()
        setContentView(viewDataBinding.root)
        with(viewModel) {
            msgText.observe(this@BaseActivity, {
                dialogHelper.msgDialog(this@BaseActivity, msg = it)
            })
            loading.observe(this@BaseActivity, {
                if (it) {
                    progressOn()
                } else {
                    progressOff()
                }
            })
        }
        initView()
    }

    protected abstract fun initBinding(): V

    protected abstract fun initView()

    protected fun progressOn() {}

    protected fun progressOff() {}

    @MainThread
    inline fun <reified VM : ViewModel> ComponentActivity.viewModels(
        noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
    ): Lazy<VM> {
        val factoryPromise = factoryProducer ?: {
            defaultViewModelProviderFactory
        }

        return ViewModelLazy(VM::class, { viewModelStore }, factoryPromise)
    }
}