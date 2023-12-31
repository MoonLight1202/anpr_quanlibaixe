package com.example.mongodbrealmcourse.model.webService

import com.example.mongodbrealmcourse.model.`object`.NumberPlateResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("plate-reader")
    suspend fun getNumberPlateDetails(
        @Header("Authorization") token: String,
        @Part imagePart : MultipartBody.Part
    ) : Response<NumberPlateResponse>
}