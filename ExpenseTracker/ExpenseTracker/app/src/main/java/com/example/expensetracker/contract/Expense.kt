package com.example.expensetracker.contract

import com.google.gson.annotations.SerializedName

data class Expense(
    @SerializedName("expenseId") var expenseId:Int=-1,
    @SerializedName("expenseAmount") var expenseAmount:Int=-1,
    @SerializedName("expenseCategory") var expenseCategory:String="",
    @SerializedName("expenseDate") var expenseDate:String=""
)
