package com.kailin.goal.ui

import androidx.lifecycle.MutableLiveData
import com.kailin.goal.api.github.GithubService
import com.kailin.goal.api.github.Repo
import com.kailin.goal.app.BaseViewModel
import kotlin.math.ceil

class MainViewModel : BaseViewModel() {

    val repoData = MutableLiveData<Pair<Int, MutableList<Repo>>>()
    private val service = okHttpHelper.create(GithubService::class.java)
    private var currSearchKeyword: String? = null
    private var currSearchPage: Int = startPage
    private var maxPage: Int = startPage

    fun searchRepo(searchKeyword: String) {
        if (currSearchKeyword == searchKeyword) {
            return
        }
        currSearchKeyword = searchKeyword
        currSearchPage = 0
        repoData.postValue(Pair(state_searchData, ArrayList()))
        searchRepoScope(startPage, state_searchData)
    }

    fun searchRepoLoadNext() {
        if (isJobActive(tag_searchRepo) || currSearchPage >= maxPage) {
            return
        }
        searchRepoScope(currSearchPage + 1, state_loadNextData)
    }

    private fun searchRepoScope(page: Int, state: Int) {
        currSearchKeyword ?: return
        val searchRepos = suspend { service.searchRepos(currSearchKeyword!!, page, pageItemCount) }
        suspendLaunch(tag_searchRepo, searchRepos) {
            val result = it.body()!!
            if (result.total_count == 0 || result.items.isNullOrEmpty()) {
                return@suspendLaunch
            }
            maxPage = ceil(result.total_count.toDouble() / pageItemCount.toDouble()).toInt()
            repoData.postValue(Pair(state, result.items))
            currSearchPage++
        }
    }

    companion object {
        private const val tag = "MainViewModel"
        private const val tag_searchRepo = "searchRepo"
        private const val startPage = 1
        private const val pageItemCount = 10
        const val state_searchData = 0
        const val state_loadNextData = 1
    }
}