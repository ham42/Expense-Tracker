package com.example.expensetracker.contract

import com.google.gson.annotations.SerializedName

data class Request(
    @SerializedName("action") var action:String="",
    @SerializedName("userId") var userId:Int=-1,
    @SerializedName("userName") var userName:String="",
    @SerializedName("userEmail") var userEmail:String="",
    @SerializedName("userPassword") var userPassword:String="",
    @SerializedName("expenseId") var expenseId:Int=-1,
    @SerializedName("expenseAmount") var expenseAmount: Int=-1,
    @SerializedName("expenseCategory") var expenseCategory:String=""
)
