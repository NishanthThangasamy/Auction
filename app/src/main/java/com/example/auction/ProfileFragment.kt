package com.example.auction

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text

class ProfileFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: FirebaseDatabase
    private lateinit var phoneNumber : TextView
    private lateinit var phone : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        var username = view.findViewById<TextView>(R.id.user_name)
        var email = view.findViewById<TextView>(R.id.user_email)
        phoneNumber = view.findViewById(R.id.user_phone)


        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val userId = sharedPreferences.getString("userId","") ?:""


        database = FirebaseDatabase.getInstance()
        val userRef = database.reference.child("users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Handle the data
                if (dataSnapshot.exists()) {
                    val userDataMap = dataSnapshot.value as? Map<*, *>
                    // Assuming user data structure is like:
                    // { "name": "John", "email": "john@example.com", "phone": "123456789", "address": "123 Street" }

                    // Set retrieved user data to TextViews
                    userDataMap?.let {
                        username.text = it["username"].toString()
                        email.text = it["email"].toString()
                        phone = it["phonenumber"].toString()
                        fillPhoneNumber(phone)
                    }

                } else {
                    // User data not found
                    Log.e("ProfileFragment", "User data not found")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("ProfileFragment", "Error getting user data", databaseError.toException())
            }
        })

        val logoutButton = view.findViewById<Button>(R.id.logout_button)

        logoutButton.setOnClickListener {
            // Clear session data
            clearSession()

            // Handle logout: Navigate to login screen or perform any other actions
            // For example:
            navigateToLoginScreen()
        }

        return view
    }
    private fun fillPhoneNumber(phone : String){
        val slicedNumber = phone.substring(4)
        phoneNumber.text = slicedNumber
    }
    // Function to clear session data
    private fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    // Function to navigate to login screen
    private fun navigateToLoginScreen() {
        // Implement your logic to navigate to the login screen
        // For example:
        val intent = Intent(requireContext(), Get_in::class.java)
        startActivity(intent)
        requireActivity().finish() // Finish the current activity to prevent going back to the profile screen
    }
}
