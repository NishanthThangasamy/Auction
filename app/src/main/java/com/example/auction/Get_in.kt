package com.example.auction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout

class Get_in : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_in)

        val SignUp = findViewById<LinearLayout>(R.id.LayoutSignUp)
        val Login = findViewById<LinearLayout>(R.id.LayoutLogin)

        SignUp.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, Resgister_MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        })
        Login.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, loginMainActivity::class.java)
            startActivity(intent)

        })
    }

}