package com.example.auction

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*


class BiddingEngineFragment : Fragment() {

    private lateinit var boxNo: TextView
    private lateinit var BidPrice: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AuctionItemAdapter
    private lateinit var databaseReference: DatabaseReference
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var boxcontainer : String
    private lateinit var bidRate : TextView
    private lateinit var timer: CountDownTimer
    private var buttonClicked = false
    private lateinit var button25 : Button
    private lateinit var button50 : Button
    private lateinit var button100 : Button
    private lateinit var button200 : Button
    private lateinit var sortedBids : MutableList<Bid>
    private lateinit var timerText : TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bidding_engine, container, false)
        boxNo = view.findViewById(R.id.boxno)
        BidPrice = view.findViewById(R.id.price)
        recyclerView = view.findViewById(R.id.leaderboard)
        bidRate = view.findViewById(R.id.bidrate)
        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("blindbiddingengine")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timerText = view.findViewById(R.id.timerTextView)

        // Initialize the timer
        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Timer ticking, do nothing
                // Update timerTextView with remaining time
                val secondsRemaining = millisUntilFinished / 1000
                timerText.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                // Timer finished, disable the page
                disablePage()
            }
        }
        button25 = view.findViewById<Button>(R.id.button25)
        button50 = view.findViewById<Button>(R.id.button50)
        button100 = view.findViewById<Button>(R.id.button100)
        button200 = view.findViewById<Button>(R.id.button200)

        // Set click listeners for the number buttons
        button25.setOnClickListener {
            onNumberClick(it)
            buttonClicked = true // Set the flag to true on button click
            timer.cancel() // Restart the timer
            timer.start()
        }
        button50.setOnClickListener {
            onNumberClick(it)
            buttonClicked = true // Set the flag to true on button click
            timer.cancel() // Restart the timer
            timer.start()
        }
        button100.setOnClickListener {
            onNumberClick(it)
            buttonClicked = true // Set the flag to true on button click
            timer.cancel() // Restart the timer
            timer.start()
        }
        button200.setOnClickListener {
            onNumberClick(it)
            buttonClicked = true // Set the flag to true on button click
            timer.cancel() // Restart the timer
            timer.start()
        }

        // Start the timer when the fragment is created
        timer.start()


        val selectedItem = arguments?.getParcelable<BlindAuctionFragment.BlindAuctionItem>("selectedItem")

        // Check if selectedItem is not null
        selectedItem?.let { item ->
            // Set the text of TextViews using the properties of selectedItem
            boxNo.text = "Container No.: ${item.boxContainer}"
            BidPrice.text = "Starting Bid: ${item.bidprice}" // Assuming bidprice is of type Long
            boxcontainer = item.boxContainer

            // Set layout manager for the RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            // Initialize and set adapter for the RecyclerView
            adapter = AuctionItemAdapter(emptyList()) // Initialize adapter with empty list initially
            recyclerView.adapter = adapter
            // Fetch data initially
            fetchDataAndUpdateUI()

            fetchHigherBidRate()
            // Schedule periodic data refresh every 1 second
            handler.postDelayed(refreshRunnable, 1000)
        }


    }

    private val refreshRunnable = object : Runnable {
        override fun run() {
            fetchDataAndUpdateUI()
            // Schedule the next execution
            fetchHigherBidRate()
            handler.postDelayed(this, 1000)
        }
    }

    private fun fetchDataAndUpdateUI() {
        // Query the database for bids with matching boxNo
        databaseReference.orderByChild("boxcontainer").equalTo(boxcontainer)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bids = mutableListOf<Bid>()
                    for (childSnapshot in snapshot.children) {
                        val username = childSnapshot.child("username").getValue(String::class.java)
                        val price = childSnapshot.child("bidprice").getValue(Long::class.java)
                        if (username != null && price != null) {
                            bids.add(Bid(username, price))
                        }
                    }

                    // Sort bids list in descending order based on price
                    sortedBids = bids.sortedByDescending { it.price }.toMutableList()
                    // Update adapter with fetched data

                    if (::adapter.isInitialized) {
                        adapter.updateData(sortedBids)
                    } else {
                        // Initialize adapter if not initialized yet
                        adapter = AuctionItemAdapter(bids)
                        recyclerView.adapter = adapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
    }
    private fun fetchHigherBidRate(){
        // Query the database for bids with matching boxNo
        databaseReference.orderByChild("boxcontainer").equalTo(boxcontainer)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var highestBidPrice = Long.MIN_VALUE // Initialize with lowest possible value
                    for (childSnapshot in snapshot.children) {
                        val price = childSnapshot.child("bidprice").getValue(Long::class.java)
                        if (price != null && price > highestBidPrice) {
                            highestBidPrice = price
                        }
                    }
                    // Update bidRate TextView with the highest bid price
                    bidRate.text = highestBidPrice.toString()

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the pending callback to avoid memory leaks
        handler.removeCallbacks(refreshRunnable)

        timer.cancel()
    }


    fun onNumberClick(view: View) {
        val numberClicked = (view as Button).text.toString().toLong()
        val bidRateText = bidRate.text.toString().toLong()


        val newBidPrice = numberClicked + bidRateText
        // Update bid price in the database
        updateBidPriceInDatabase(newBidPrice)
    }

    private fun updateBidPriceInDatabase(newBidPrice: Long) {
        // Retrieve the ID from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")

        // Update the bid price in the database
        val bidPriceReference = databaseReference.child(userId!!).child("bidprice")
        bidPriceReference.setValue(newBidPrice)
            .addOnSuccessListener {
                // Update successful
                // You can handle success actions here
                Log.d("BiddingEngineFragment", "Bid price updated successfully: $newBidPrice")
            }
            .addOnFailureListener { e ->
                // Handle any errors
                Log.d("BiddingEngineFragment", "Bid price update failure: $e")
            }
    }

    // Function to disable the page
    @SuppressLint("SetTextI18n")
    private fun disablePage() {
        // Disable the buttons
        button25.isEnabled = false
        button50.isEnabled = false
        button100.isEnabled = false
        button200.isEnabled = false

        // Move to the next or previous page based on conditions
        // Here, you need to implement the logic to move to the next or previous page
        // based on the current user's bid and other conditions
        // For example:
        currentUserBidMatchesFirstBid { matches ->

            if (matches) {


                        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirmation, null)
                        val dialogBuilder = AlertDialog.Builder(requireContext())
                            .setView(dialogView)
                        val dialog = dialogBuilder.create()
                        dialog.show()

                        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
                        val userId = sharedPreferences.getString("userId", "")


                dialogView.findViewById<Button>(R.id.buttonOk).setOnClickListener {
                            dialog.dismiss() // Dismiss dialog if OK is clicked
                            // Move to the next page
                            val nextFragment = UnderConstruction().apply {
                                 arguments = Bundle().apply {
                                    putString("userId", userId)
                                    putString("boxContainer", boxcontainer)
                                     putString("BidRate", bidRate.text.toString())
                                // You can put additional data if needed
                                }
                        }
                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.frame_container, nextFragment)
                            transaction.commit()
                        }

            } else {
                val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirmation, null)
                val dialogBuilder = AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                val dialog = dialogBuilder.create()
                dialog.show()

                val message = dialogView.findViewById<TextView>(R.id.textViewMessage)
                message.text= "Your bid is closed"

                dialogView.findViewById<Button>(R.id.buttonOk).setOnClickListener {
                    dialog.dismiss() // Dismiss dialog if OK is clicked
                    // Move to the previous page
                    // End current page (close current fragment)
                    val nextFragment = BlindAuctionFragment() // Replace NextFragment with your actual next fragment class
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frame_container, nextFragment)
                    transaction.commit()
//                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    // Function to check if the current user's bid matches the first bid in the list
    private fun currentUserBidMatchesFirstBid(callback: (Boolean) -> Unit) {
        // Implement your logic to compare the current user's bid with the first bid in the list
        // Return true if the bids match, false otherwise
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")
        val currentUserBidReference = databaseReference.child(userId!!)
        currentUserBidReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUserBidUsername = snapshot.child("username").getValue(String::class.java)

                if (currentUserBidUsername != null) {
                    val firstBidUsername = sortedBids.firstOrNull()?.username
                    Toast.makeText(context, "Bid Winner: $firstBidUsername", Toast.LENGTH_SHORT).show()
                    val match = firstBidUsername == currentUserBidUsername
                    callback(match)
                }else {
                    callback(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                callback(false)
            }
        })
    }

    data class Bid(val username: String, val price: Long)
}
