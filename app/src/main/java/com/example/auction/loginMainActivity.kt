package com.example.auction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.content.SharedPreferences

class loginMainActivity : AppCompatActivity() {

    private lateinit var editTextLoginUser: EditText
    private lateinit var editTextLoginPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var textViewSignUp: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_main)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("users") // "users" is the node in the Realtime Database
        editTextLoginUser = findViewById(R.id.editTextLoginUser)
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        textViewSignUp = findViewById(R.id.textViewSignUp)
        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        buttonLogin.setOnClickListener {
            val email = editTextLoginUser.text.toString().trim()
            val password = editTextLoginPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        textViewSignUp.setOnClickListener {
            // Navigate to SignUpActivity
            val intent = Intent(this, Resgister_MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userData = userSnapshot.value as Map<*, *>
                        val storedPassword = userData["password"] as String
                        if (storedPassword == password) {
                            // Password matches
                            val userId = userSnapshot.key
                            // Store user information locally (e.g., using SharedPreferences)
                            saveUserDataLocally(email, userId)
                            // Navigate to the next activity or perform any other action
                            val intent = Intent(this@loginMainActivity, BottomNavActivity::class.java)
                            startActivity(intent)
                            finish()

                             6999
                        }
                    }

                    Toast.makeText(this@loginMainActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                } else {
                    // User with provided email not found
                    Toast.makeText(this@loginMainActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Toast.makeText(this@loginMainActivity, "Failed to fetch user data: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    private fun saveUserDataLocally(email: String, userId: String) {
//        // Store user information locally using SharedPreferences or any other suitable method
//        // Example:
//        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
//        with(sharedPreferences.edit()) {
//
//            putString("userEmail", email)
//            putString("userId", userId)
//            // Add any other user information you want to store locally
//            apply()
//        }
//    }


    private fun saveUserDataLocally(email: String, userId: String?) {
        // Store user email and userId in local storage
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
       userId?.let {
           editor.putString("userId", userId)

           // Retrieve user phone number from Firebase Realtime Database
           databaseReference.child(userId)
               .addListenerForSingleValueEvent(object : ValueEventListener {
                   override fun onDataChange(snapshot: DataSnapshot) {
                       val phoneNumber = snapshot.child("phonenumber").value.toString()
                       val username = snapshot.child("username").value.toString()
                       // Store user phone number in local storage
                       editor.putString("phoneNumber", phoneNumber)
                       editor.putString("username", username)
                       editor.apply()
                   }

                   override fun onCancelled(error: DatabaseError) {
                       // Handle error
                   }
               })
       }
        editor.apply()
    }
}
