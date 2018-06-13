package com.tinashe.audioverse.data.api

import com.tinashe.audioverse.data.model.response.PresentersResponse
import com.tinashe.audioverse.data.model.response.RecordingsResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AudioVerseApi {

    @GET("recordings")
    fun getNewRecordings(): Observable<Response<RecordingsResponse>>

    @GET("recordings/popular")
    fun getTrendingRecordings(): Observable<Response<RecordingsResponse>>

    @GET("recordings/featured")
    fun getFeaturedRecordings(): Observable<Response<RecordingsResponse>>

    @GET("recordings/presenter/{id}")
    fun getPresenterRecordings(@Path("id") presenterId: String): Observable<Response<RecordingsResponse>>

    @GET("presenters?all=true")
    fun getPresenters(): Observable<Response<PresentersResponse>>

    @GET("presenters?all=true")
    fun listPresenters(): Call<PresentersResponse>


}