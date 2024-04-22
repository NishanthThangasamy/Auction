package com.example.auction



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private var lastClickTime: Long = 0
    private val cooldownDuration: Long = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.menu.findItem(R.id.navigation_home)?.isChecked = true
        bottomNavigationView.setOnItemSelectedListener { item ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= cooldownDuration) {
                // Update last click time
                lastClickTime = currentTime

                when (item.itemId) {
                    R.id.navigation_home -> {
                        // Start Home_MainActivity2
                        replaceFragment(Home_acitivity())
                        true
                    }

                    R.id.navigation_auction -> {
                        // Start Sell_MainActivity3
                        replaceFragment(AuctionFragment())
                        true
                    }

                    R.id.navigation_auction_history -> {
                        // Handle auction history navigation
                        // You can start the activity for auction history here
                        replaceFragment(HistroyFragment())
                        true
                    }

                    R.id.navigation_blind_auction -> {
                        // Handle blind auction navigation
                        // You can start the activity for blind auction here
                        replaceFragment(BlindAuctionFragment())
                        true
                    }

                    R.id.navigation_account_auction -> {
                        replaceFragment(ProfileFragment())
                        true
                    }
                    else -> false
                }  }else {
                // Click is within cooldown duration, do nothing
                true
            }
        }
        replaceFragment(Home_acitivity())

    }
    private fun replaceFragment(fragment: Fragment)
    {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit()
    }

    // After setOnNavigationItemSelectedListener


}
