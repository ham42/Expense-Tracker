package com.example.expensetracker

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.system.Os.remove
import android.view.View
import com.example.expensetracker.contract.Expense
import com.example.expensetracker.contract.Request
import com.example.expensetracker.contract.Response
import com.example.expensetracker.network.IRequestContract
import com.example.expensetracker.network.NetworkClient
import com.example.expensetracker.utils.Constant
import com.example.expensetracker.utils.DataProvider
import com.example.expensetracker.utils.showToast
import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Call
import retrofit2.Callback

class HomeActivity : AppCompatActivity(),  View.OnClickListener, Callback<Response> {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract = retrofitClient.create(IRequestContract::class.java)
    private lateinit var userId:String
    private lateinit var userName:String
    private lateinit var userEmail:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)
        userId = sharedPreferences.getString(Constant.KEY_USER_ID, "").toString()
        userName = sharedPreferences.getString(Constant.KEY_USER_NAME, "").toString()
        userEmail = sharedPreferences.getString(Constant.KEY_USER_EMAIL, "").toString()

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("please wait...")
        progressDialog.setCancelable(true)

        title = "Expenses Tracker"
        txtUserName.text = "Username: ${userName}"
        txtUserEmail.text = "Email: ${userEmail}"

        btnAddExpense.setOnClickListener(this)
        btnViewExpense.setOnClickListener(this)
        btnLogout.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        progressDialog.show()
        val request = Request(
            action = Constant.GET_EXPENSES,
            userId = userId.toString().toInt()
        )
        val callResponse = requestContract.makeApiCall(request)
        callResponse.enqueue(this)
    }

    override fun onClick(v: View?){
        when(v?.id){
            R.id.btnAddExpense -> {
                Intent(this, AddExpenseActivity::class.java).apply {
                    putExtra("userId", userId)
                    startActivity(this)
                }
            }

            R.id.btnViewExpense -> {
                if(DataProvider.response.allExpenses.size>0){
                    Intent(this, ViewExpensesActivity::class.java).apply {
                        startActivity(this)
                    }
                } else{
                    showToast("Expenses are Not Available")
                }
            }

            R.id.btnLogout -> {
                sharedPreferences.edit().clear().commit()
                DataProvider.response = Response()
                DataProvider.expense = Expense()
                Intent(this, MainActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            }
        }
    }

    override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()
        if(response.body()!=null){
            val serverResponse = response.body()
            if(serverResponse!!.status){
                DataProvider.response = serverResponse
            }
        }
    }

    override fun onFailure(call: Call<Response>, t: Throwable) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()
        showToast("Server is not responding. Please contact your system administrator")
    }
}