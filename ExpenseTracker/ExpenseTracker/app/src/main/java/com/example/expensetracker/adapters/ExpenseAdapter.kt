package com.example.expensetracker.adapters

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.AddExpenseActivity
import com.example.expensetracker.R
import com.example.expensetracker.contract.Expense
import com.example.expensetracker.contract.Request
import com.example.expensetracker.contract.Response
import com.example.expensetracker.network.IRequestContract
import com.example.expensetracker.network.NetworkClient
import com.example.expensetracker.utils.Constant
import com.example.expensetracker.utils.DataProvider
import com.example.expensetracker.utils.showToast
import kotlinx.android.synthetic.main.expense_item.view.*
import retrofit2.Call
import retrofit2.Callback

class ExpenseAdapter(var activity: Activity, var context: Context, var dataSource:MutableList<Expense>): RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>(),
    Callback<Response> {

    private var progressDialog: ProgressDialog = ProgressDialog(context)
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract = retrofitClient.create(IRequestContract::class.java)
    private lateinit var deletedExpense: Expense
    private var deletedPosition: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.expense_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = dataSource[position]
        holder.amount.text = expense.expenseAmount.toString()
        holder.category.text = expense.expenseCategory
        holder.date.text = expense.expenseDate

        holder.btnEdit.setOnClickListener {
            Intent(context, AddExpenseActivity::class.java).apply {
                DataProvider.expense = expense
                putExtra(Constant.KEY_REASON, 2)  //2 Means Edit
                activity.startActivity(this)
            }
        }

        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Expense App Alert")
                .setMessage("Are you sure? You want to delete this Expense")
                .setPositiveButton("Yes") { dialog, which ->
                    progressDialog.setMessage("Please wait...")
                    progressDialog.setCancelable(false)
                    deletedExpense = expense
                    deletedPosition = position
                    val request = Request(
                        action = Constant.DELETE_EXPENSE,
                        expenseId = expense.expenseId.toString().toInt()
                    )
                    progressDialog.show()
                    val callResponse = requestContract.makeApiCall(request)
                    callResponse.enqueue(this)
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog?.dismiss()
                }
                .create()
                .show()
        }
    }

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var amount = view.amount
        var category = view.category
        var date = view.date
        var btnEdit = view.edit
        var btnDelete = view.delete
    }

    override fun onFailure(call: Call<Response>, t: Throwable) {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
        context.showToast("Server is not responding. Please contact your system administrator")
    }

    override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
        if (response.body() != null) {
            val serverResponse = response.body()
            if (serverResponse!!.status) {
                dataSource.remove(deletedExpense)
                notifyItemRemoved(deletedPosition)
                notifyItemRangeChanged(deletedPosition, dataSource.size)
                context.showToast(serverResponse.message)
            } else {
                context.showToast(serverResponse.message)
            }
        } else {
            context.showToast("Server is not responding. Please contact your system administrator")
        }
    }
}


