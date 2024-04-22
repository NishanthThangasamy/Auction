package com.example.auction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class SignUpMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_main)

        val verifiedPhoneNumber = intent.getStringExtra("PhoneNumber") ?: ""
        val userId = intent.getStringExtra("users")
        val login = findViewById<TextView>(R.id.gotologin)
        val signUpButton = findViewById<Button>(R.id.Signup)

        val phoneNumeber = findViewById<TextView>(R.id.editTextPhoneNumber)

        fillPhoneNumber(verifiedPhoneNumber , phoneNumeber)

        signUpButton.setOnClickListener {
            val username = findViewById<EditText>(R.id.editTextUsername).text.toString().trim()
            val email = findViewById<EditText>(R.id.editTextEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString().trim()
            val confirmPassword = findViewById<EditText>(R.id.repassword).text.toString().trim()

            // Check if passwords match
            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get reference to Firebase Database
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")
            val tokensRef = database.getReference("tokens")
            //Get the FCM token
            FirebaseMessaging.getInstance().token.addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    val fcmToken = task.result
                    if(!fcmToken.isNullOrEmpty()){
                        tokensRef.child(userId!!).setValue(fcmToken)
                            .addOnCompleteListener{
                                // Create a map to store user details
                                val userMap = HashMap<String, Any>()
                                userMap["username"] = username
                                userMap["email"] = email
                                userMap["password"] = password
                                userMap["phonenumber"] = verifiedPhoneNumber
                                // Set the user details under the userId within the "users" collection
                                if (userId != null) {
                                    usersRef.child(userId).setValue(userMap)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "User details saved successfully", Toast.LENGTH_SHORT).show()
                                            // Navigate to next activity or perform any other
                                            val intent = Intent(this, loginMainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Failed to save user details: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                else{
                                    Toast.makeText(this, "userid not", Toast.LENGTH_SHORT).show()

                                }
                            } .addOnFailureListener {e->
                                Toast.makeText(this, "Failed to save FCM token: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "FCM token is null or empty", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Failed to get FCM token: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }


        }
        login.setOnClickListener{
            val intent = Intent(this, loginMainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun fillPhoneNumber(phoneNumber: String, number : TextView){
        val slicedNumber = phoneNumber.substring(4)
            number.text = slicedNumber
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        // Start the Get_in activity when back button is pressed
        val intent = Intent(this, Get_in::class.java)
        startActivity(intent)
        finish()
    }
}
