package com.example.auction

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OtpVerify : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private var phoneNumber: String? = null
    private lateinit var otpEditText1: EditText
    private lateinit var otpEditText2: EditText
    private lateinit var otpEditText3: EditText
    private lateinit var otpEditText4: EditText
    private lateinit var otpEditText5: EditText
    private lateinit var otpEditText6: EditText
    private lateinit var clipboardManager: ClipboardManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verify)

        auth = FirebaseAuth.getInstance()

        val verifyOtpButton = findViewById<Button>(R.id.btn_verify_Otp)

        verificationId = intent.getStringExtra("verificationId")
        phoneNumber = intent.getStringExtra("phoneNumber")

        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        otpEditText1 = findViewById(R.id.digit1)
        otpEditText2 = findViewById(R.id.digit2)
        otpEditText3 = findViewById(R.id.digit3)
        otpEditText4 = findViewById(R.id.digit4)
        otpEditText5 = findViewById(R.id.digit5)
        otpEditText6 = findViewById(R.id.digit6)

        otpEditText1.addTextChangedListener(OtpTextWatcher(otpEditText1, otpEditText2))
        otpEditText2.addTextChangedListener(OtpTextWatcher(otpEditText2, otpEditText3))
        otpEditText3.addTextChangedListener(OtpTextWatcher(otpEditText3, otpEditText4))
        otpEditText4.addTextChangedListener(OtpTextWatcher(otpEditText4, otpEditText5))
        otpEditText5.addTextChangedListener(OtpTextWatcher(otpEditText5, otpEditText6))

        setBackspaceListener(otpEditText1, null);
        setBackspaceListener(otpEditText2, otpEditText1);
        setBackspaceListener(otpEditText3, otpEditText2);
        setBackspaceListener(otpEditText4, otpEditText3);
        setBackspaceListener(otpEditText5, otpEditText4);
        setBackspaceListener(otpEditText6, otpEditText5);

        verifyOtpButton.setOnClickListener {
            val otp = otpEditText1.text.toString()+
                    otpEditText2.text.toString()+
                    otpEditText3.text.toString() +
                    otpEditText4.text.toString() +
                    otpEditText5.text.toString() +
                    otpEditText6.text.toString()

            if (otp.isNotEmpty()) {
                verifyOtp(otp)
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }
        clipboardManager.addPrimaryClipChangedListener {
            val clipData = clipboardManager.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                val copiedText = clipData.getItemAt(0).text.toString()
                if (copiedText.length == 6 && copiedText.all { it.isDigit() }) {
                    pasteOtpFromClipboard(copiedText)
                }
            }
        }
    }
    private fun verifyOtp(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
        signInWithPhoneAuthCredential(credential)
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    user?.let {
                        val userId = it.uid
                        checkUserExists(userId)
                    } ?: run {
                        Toast.makeText(this, "User is null", Toast.LENGTH_SHORT).show()
                    }
                    // Proceed with your app logic after successful authentication
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserExists(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists in the database
                    Toast.makeText(this@OtpVerify, "User already Exists", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@OtpVerify, loginMainActivity::class.java)
                    startActivity(intent)
                    finish() // Finish current activity to prevent going back
                } else {
                    // User does not exist in the database
                    val intent = Intent(this@OtpVerify, SignUpMainActivity::class.java)
                    intent.putExtra("PhoneNumber", phoneNumber)
                    intent.putExtra("users", userId)
                    startActivity(intent)
                    finish() // Finish current activity to prevent going back
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Toast.makeText(this@OtpVerify, "Failed to check user existence: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private class OtpTextWatcher(
        private val currentView: EditText,
        private val nextView: EditText
    ) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (s?.length == 1) {
                nextView.requestFocus()
            }
        }
    }
    private fun setBackspaceListener(currentEditText: EditText, previousEditText: EditText?) {
        currentEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (currentEditText.text.toString().isEmpty() && previousEditText != null) {
                    // If current EditText is empty and there's a previous EditText, move focus to previous EditText
                    previousEditText.requestFocus()
                    return@setOnKeyListener true // consume the event
                }
            }
            false // let other key events be handled
        }
    }
    private fun pasteOtpFromClipboard(otp: String) {
        otpEditText1.setText(otp.substring(0, 1))
        otpEditText2.setText(otp.substring(1, 2))
        otpEditText3.setText(otp.substring(2, 3))
        otpEditText4.setText(otp.substring(3, 4))
        otpEditText5.setText(otp.substring(4, 5))
        otpEditText6.setText(otp.substring(5, 6))
    }
}
