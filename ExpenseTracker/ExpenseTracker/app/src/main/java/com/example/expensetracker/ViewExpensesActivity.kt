package com.example.expensetracker

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.expensetracker.adapters.ExpenseAdapter
import com.example.expensetracker.contract.Expense
import com.example.expensetracker.utils.DataProvider
import kotlinx.android.synthetic.main.activity_view_expenses.*

class ViewExpensesActivity : AppCompatActivity() {
    lateinit var adapter: ExpenseAdapter
    lateinit var dataSource:MutableList<Expense>
    private lateinit var context: Context
    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_expenses)

        context = this
        activity = this
        dataSource = DataProvider.response.allExpenses

       if(dataSource.size>0){
            adapter = ExpenseAdapter(activity,context,dataSource)
            rvExpenseList.adapter = adapter
        }
    }
}