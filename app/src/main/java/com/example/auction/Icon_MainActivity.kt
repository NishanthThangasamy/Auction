package com.example.auction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Icon_MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_icon_main)
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserSession()
    },2000)

}
    private fun checkUserSession() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
//        val userToken = sharedPreferences.getString("userToken", null)
        val userEmail = sharedPreferences.getString("email", "")
        val userId = sharedPreferences.getString("userId","")
        // Check if user token and email are not null and not empty
        if (!userEmail.isNullOrEmpty() && !userId.isNullOrEmpty()) {
            // Proceed with checking the database using the token and email
            navigateToUserHomePage()
        } else {
            // If user token or email is not found or empty, navigate to the login page
            navigateToLoginPage()
        }
    }
//    Retrieve the user token and email from SharedPreferences
    private fun navigateToUserHomePage() {
        val intent = Intent(this, BottomNavActivity::class.java)

        startActivity(intent)
        finish() // Close the current activity
    }
    private fun navigateToLoginPage() {
        // Implement your logic to navigate to the login page
        // For example:
        val intent = Intent(this, Get_in::class.java)
        startActivity(intent)
        finish() // Finish the current activity, as we don't want the user to go back to the login page
    }
}









