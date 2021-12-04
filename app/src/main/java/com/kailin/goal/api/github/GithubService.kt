package com.kailin.goal.api.github

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubService {

    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") searchKeyword: String = "",
        @Query("page") page: Int = 1,
        @Query("per_page") per_page: Int = 30,
    ): Response<SearchRepoResult>
}