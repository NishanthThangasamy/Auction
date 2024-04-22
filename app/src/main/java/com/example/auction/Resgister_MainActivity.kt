package com.example.auction

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class Resgister_MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_main)

        auth = FirebaseAuth.getInstance()

        val phoneEditText = findViewById<EditText>(R.id.phone)
        val generateOtpButton = findViewById<Button>(R.id.btn_generate_Otp)
        val backButton = findViewById<ImageView>(R.id.backButtom)
//        val verifyOtpButton = findViewById<Button>(R.id.btn_verify_Otp)

        generateOtpButton.setOnClickListener {
            val phoneNumber = phoneEditText.text.toString().trim()
            val phone = "+91 $phoneNumber"
            if (phone.isNotEmpty()) {
                sendOtp(phone)
            } else {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
            }
        }
        backButton.setOnClickListener {
            val intent = Intent(this@Resgister_MainActivity, Get_in::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun sendOtp(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(
                        this@Resgister_MainActivity,
                        "Verification failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@Resgister_MainActivity.verificationId = verificationId
                    // Start the next activity and pass the verificationId as an extra
                    val intent = Intent(this@Resgister_MainActivity, OtpVerify::class.java)
                    intent.putExtra("verificationId", verificationId)
                    intent.putExtra("phoneNumber", phoneNumber)
                    startActivity(intent)
                    finish()
                }
            })
    }

}

//    private fun verifyOtp(otp: String) {
//        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
//        signInWithPhoneAuthCredential(credential)
//    }

//    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val user = task.result?.user
//
//                    Toast.makeText(this, "Authentication successful", Toast.LENGTH_SHORT).show()
//                    val i = Intent(this,BottomNavActivity::class.java)
//                    startActivity(i)
//                    finish()
//                    // Proceed with your app logic after successful authentication
//                } else {
//                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//    override fun onBackPressed() {
//        super.onBackPressed()
//        // Start the Get_in activity when back button is pressed
//        val intent = Intent(this, Get_in::class.java)
//        startActivity(intent)
//        finish()
//    }



