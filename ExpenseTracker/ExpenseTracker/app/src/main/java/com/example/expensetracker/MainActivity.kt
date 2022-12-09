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
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback

class MainActivity : AppCompatActivity(), Callback<Response> {
    private lateinit var progressDialog: ProgressDialog
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract = retrofitClient.create(IRequestContract::class.java)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userEmail:String
    private lateinit var userPassword:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("please wait...")
        progressDialog.setCancelable(true)

        sharedPreferences = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)

        checkIfUserAlreadyRegistered()

        btnLogin.setOnClickListener(){
            userEmail = enEmail.text.toString().trim()
            userPassword = enPassword.text.toString().trim()

            if(userEmail.isNullOrEmpty() || userPassword.isNullOrEmpty()){
                showToast("Please Fill the Required Fields")
            } else{
                progressDialog.show()
                val request = Request(
                    action = Constant.LOG_IN,
                    userEmail = userEmail,
                    userPassword = userPassword
                )
                val callResponse = requestContract.makeApiCall(request)
                callResponse.enqueue(this)
            }
        }

        btnSignup.setOnClickListener(){
            Intent(this, SignupActivity::class.java).apply {
                startActivity(this)
            }
        }


    }

    override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()

        if(response.body()!=null) {
            val serverResponse = response.body()
            if(serverResponse!!.status) {
                saveUserToPref(serverResponse.userId.toString(), serverResponse.userName, userEmail, userPassword)
                Intent(this, HomeActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            } else{
                showToast(serverResponse.message)
                enEmail.setText("")
                enPassword.setText("")
            }
        }
    }

    override fun onFailure(call: Call<Response>, t: Throwable) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()

        showToast("Server is not responding. Please contact your system administrator")
        enEmail.setText("")
        enPassword.setText("")
    }

    private fun saveUserToPref(userId:String, userName: String, userEmail: String, userPassword: String) {
        val editor = sharedPreferences.edit()
        editor.putString(Constant.KEY_USER_ID,userId)
        editor.putString(Constant.KEY_USER_NAME,userName)
        editor.putString(Constant.KEY_USER_EMAIL,userEmail)
        editor.putString(Constant.KEY_USER_PASSWORD,userPassword)
        editor.commit()
    }

    private fun checkIfUserAlreadyRegistered() {
        val userId = sharedPreferences.getString(Constant.KEY_USER_ID,"invalid userId")
        val userName = sharedPreferences.getString(Constant.KEY_USER_NAME,"invalid userName")
        val userEmail = sharedPreferences.getString(Constant.KEY_USER_NAME,"invalid userEmail")
        val userPassword = sharedPreferences.getString(Constant.KEY_USER_PASSWORD,"invalid userPassword")

        if(!userId.contentEquals("invalid userId") &&
            !userName.contentEquals("invalid userName") &&
            !userEmail.contentEquals("invalid userEmail") &&
            !userPassword.contentEquals("invalid userPassword")){
            Intent(this,HomeActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }
    }
}