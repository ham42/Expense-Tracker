package com.example.expensetracker.network

import com.example.expensetracker.contract.Request
import com.example.expensetracker.contract.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IRequestContract {
    @POST("service.php")
    fun makeApiCall(@Body request: Request): Call<Response>
}