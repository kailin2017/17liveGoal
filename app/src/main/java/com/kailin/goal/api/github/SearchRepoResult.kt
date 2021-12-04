package com.kailin.goal.api.github

data class SearchRepoResult(
    val incomplete_results: Boolean = false,
    val items: MutableList<Repo> = ArrayList(),
    val total_count: Int = 0,
): GithubResult()