package com.example.expensetracker

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.expensetracker.contract.Expense
import com.example.expensetracker.contract.Request
import com.example.expensetracker.contract.Response
import com.example.expensetracker.network.IRequestContract
import com.example.expensetracker.network.NetworkClient
import com.example.expensetracker.utils.Constant
import com.example.expensetracker.utils.DataProvider
import com.example.expensetracker.utils.showToast
import kotlinx.android.synthetic.main.activity_add_expense.*
import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Call
import retrofit2.Callback

class AddExpenseActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, Callback<Response> {
    private lateinit var progressDialog: ProgressDialog
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract = retrofitClient.create(IRequestContract::class.java)
    private var reason:Int = 0
    private lateinit var editedExpense: Expense
    private lateinit var userId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("please wait...")
        progressDialog.setCancelable(true)

        setExpCategorySpinner()

        userId = getIntent().extras?.getString("userId").toString()
        reason = intent.getIntExtra(Constant.KEY_REASON,0)

        renderUIForEdit()

        addExpense.setOnClickListener(){
            val expenseAmount = enExpenseAmount.text.toString().trim()
            val expenseCategory = expCategory.selectedItem.toString()
            var request = Request()

            if(expenseAmount.isNotEmpty()){
                try {
                    expenseAmount.toInt()
                    if(expenseCategory == "Select Category"){
                        showToast("Please Select Expense Category")
                    } else{
                        if(reason==2){
                            request = Request(
                                action = Constant.UPDATE_EXPENSE,
                                expenseId = editedExpense.expenseId,
                                expenseAmount = expenseAmount.toString().toInt(),
                                expenseCategory = expenseCategory,
                            )
                        } else{
                            request = Request(
                                action = Constant.ADD_EXPENSE,
                                expenseAmount = expenseAmount.toString().toInt(),
                                expenseCategory = expenseCategory,
                                userId = userId.toString().toInt()
                            )
                        }
                        progressDialog.show()
                        val callResponse = requestContract.makeApiCall(request)
                        callResponse.enqueue(this)
                    }
                } catch (e: NumberFormatException) {
                    showToast("Please Enter Valid Amount")
                }
            } else{
                showToast("Please Fill the Amount Field")
            }


        }
    }

    private fun setExpCategorySpinner(){
        val adapter = ArrayAdapter.createFromResource(this, R.array.expCategory_array, android.R.layout.simple_dropdown_item_1line)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        expCategory.adapter = adapter
        expCategory.onItemSelectedListener = this
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    private fun renderUIForEdit(){
        if(reason==2){
            editedExpense = DataProvider.expense
            enExpenseAmount.setText(editedExpense.expenseAmount.toString())
            addExpense.text = "UPDATE"
        }
    }

    override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()

        if(response.body()!= null){
            val serverResponse = response.body()
            if(serverResponse!!.status){
                showToast(serverResponse.message)
                Intent(this, HomeActivity::class.java).apply {
                    startActivity(this)
                    finishAffinity()
                }
            } else{
                showToast(serverResponse.message)
                enExpenseAmount.setText("")
            }
        }
    }

    override fun onFailure(call: Call<Response>, t: Throwable) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()

        showToast("Server is not Responding. Please Contact your Administrator")
        enExpenseAmount.setText("")
    }
}