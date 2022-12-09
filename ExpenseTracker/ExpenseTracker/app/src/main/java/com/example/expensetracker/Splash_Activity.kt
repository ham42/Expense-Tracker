package com.example.expensetracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.expensetracker.utils.Configuration
import kotlinx.android.synthetic.main.activity_splash.*

class Splash_Activity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sharedPreferences = getSharedPreferences("IP_APP", Context.MODE_PRIVATE)
        if (sharedPreferences.getString("IP", "") != "") {
            Intent(this, MainActivity::class.java).apply {
                startActivity(this)
                finish()

            }

        }
    }
    fun add_ip(view: View) {

        var IP = ip_address.text.toString().trim()

        sharedPreferences.edit().putString("IP", IP).commit()

        Configuration.BASE_URL = "http://$IP:80/EXPENSE_TRACKER/";

        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
            finish()
        }

    }
}
