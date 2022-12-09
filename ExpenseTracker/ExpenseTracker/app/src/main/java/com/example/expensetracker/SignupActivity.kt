package com.example.expensetracker

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.expensetracker.contract.Request
import com.example.expensetracker.contract.Response
import com.example.expensetracker.network.IRequestContract
import com.example.expensetracker.network.NetworkClient
import com.example.expensetracker.utils.Constant
import com.example.expensetracker.utils.showToast
import kotlinx.android.synthetic.main.activity_signup.*
import retrofit2.Call
import retrofit2.Callback

class SignupActivity : AppCompatActivity(), Callback<Response> {
    private lateinit var progressDialog: ProgressDialog
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract = retrofitClient.create(IRequestContract::class.java)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userName:String
    private lateinit var userEmail:String
    private lateinit var userPassword:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("please wait...")
        progressDialog.setCancelable(true)

        sharedPreferences = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)

        btnRegister.setOnClickListener(){
            userName = enUserName.text.toString().trim()
            userEmail = enUserEmail.text.toString().trim()
            userPassword = enUserPassword.text.toString().trim()

            if(userName.isNullOrEmpty() || userEmail.isNullOrEmpty() || userPassword.isNullOrEmpty()){
                showToast("Please Fill the Required Fields")
            } else{
                progressDialog.show()
                val request = Request(
                    action = Constant.SIGN_UP,
                    userName = userName,
                    userEmail = userEmail,
                    userPassword = userPassword
                )
                val callResponse = requestContract.makeApiCall(request)
                callResponse.enqueue(this)
            }
        }
    }

    override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()

        if(response.body()!=null) {
            val serverResponse = response.body()
            if(serverResponse!!.status) {
                saveUserToPref(serverResponse.userId.toString(), userName, userEmail, userPassword)
                Intent(this, HomeActivity::class.java).apply {
                    startActivity(this)
                    finishAffinity()
                }
            } else{
                showToast(serverResponse.message)
                enUserName.setText("")
                enUserEmail.setText("")
                enUserPassword.setText("")
            }
        }
    }

    override fun onFailure(call: Call<Response>, t: Throwable) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()

        showToast("Server is not responding. Please contact your system administrator")
        enUserName.setText("")
        enUserEmail.setText("")
        enUserPassword.setText("")
    }

    private fun saveUserToPref(userId:String, userName: String, userEmail: String, userPassword: String) {
        val editor = sharedPreferences.edit()
        editor.putString(Constant.KEY_USER_ID,userId)
        editor.putString(Constant.KEY_USER_NAME,userName)
        editor.putString(Constant.KEY_USER_EMAIL,userEmail)
        editor.putString(Constant.KEY_USER_PASSWORD,userPassword)
        editor.commit()
    }
}