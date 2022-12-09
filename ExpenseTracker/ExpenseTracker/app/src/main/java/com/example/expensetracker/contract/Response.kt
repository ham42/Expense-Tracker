package com.example.expensetracker.contract

data class Response(
    var status:Boolean = false,
    var responseCode:Int = -1,
    var message:String = "",
    var userId:Int = -1,
    var userName:String = "",
    var expenseId:Int = -1,
    var allExpenses:MutableList<Expense> = mutableListOf()
)
