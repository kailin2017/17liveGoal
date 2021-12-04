package com.kailin.goal.app

import androidx.lifecycle.*
import com.kailin.goal.api.github.GithubResult
import com.kailin.goal.utils.OkHttpHelper
import kotlinx.coroutines.*
import retrofit2.Response
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.experimental.ExperimentalTypeInference

abstract class BaseViewModel : ViewModel() {

    val msgText: MutableLiveData<String> by lazy { MutableLiveData() }
    val loading: MutableLiveData<Boolean> by lazy { MutableLiveData(false) }
    protected val okHttpHelper: OkHttpHelper by lazy { OkHttpHelper.instance }
    protected val jobMap = HashMap<String, Job>()

    fun onError(e: Throwable) {
        e.printStackTrace()
        msgText.postValue(e.message)
    }

    @OptIn(ExperimentalTypeInference::class)
    fun <T> liveData(
        context: CoroutineContext = EmptyCoroutineContext,
        timeoutInMs: Long = 5000L,
        @BuilderInference block: suspend LiveDataScope<T>.() -> Unit
    ): LiveData<T> = try {
        androidx.lifecycle.liveData(context, timeoutInMs, block)
    } catch (e: Exception) {
        onError(e)
        MutableLiveData()
    }

    protected fun scopeLaunch(tag: String, unit: suspend CoroutineScope.() -> Unit) {
        cancelJobs(tag)
        loading.postValue(true)
        jobMap[tag] = viewModelScope.launch {
            unit()
            loading.postValue(false)
        }
    }

    protected fun <T : GithubResult> suspendLaunch(
        tag: String,
        unit: suspend () -> Response<T>,
        callback: (Response<T>) -> Unit
    ) {
        cancelJobs(tag)
        loading.postValue(true)
        jobMap[tag] = viewModelScope.launch {
            val response = unit()
            when {
                response.isSuccessful -> {
                    callback(response)
                }
                response.body() != null -> {
                    msgText.postValue(response.body()!!.message)
                }
                else -> {
                    msgText.postValue(response.message())
                }
            }
            loading.postValue(false)
        }
    }

    fun cancelAllJobs() {
        jobMap.values.forEach { it.cancel() }
    }

    fun cancelJobs(vararg tags: String) {
        tags.iterator().forEach { jobMap[it]?.cancel() }
    }

    fun isJobActive(tag: String): Boolean {
        return jobMap[tag]?.isActive ?: false
    }

//    fun getString(@StringRes stringId: Int): String {
//        return StringUtil.instance.getString(stringId)
//    }
}