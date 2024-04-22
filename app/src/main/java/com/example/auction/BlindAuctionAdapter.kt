package com.example.auction

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import android.os.Handler
import android.os.Looper
import android.content.Context.MODE_PRIVATE
import android.os.Message
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.RemoteMessage


class BlindAuctionAdapter(private val context: Context, private val viewModel: AuctionViewModel) : RecyclerView.Adapter<BlindAuctionAdapter.BlindAuctionViewHolder>() {

    private var data: List<BlindAuctionFragment.BlindAuctionItem> = emptyList()
    //    private var timers: MutableMap<Int, CountDownTimer?> = mutableMapOf()
    private val handler = Handler(Looper.getMainLooper())

        private val refreshIntervalMillis = 1000L // 1 second

        private val refreshRunnable = object : Runnable {
            override fun run() {
                notifyDataSetChanged()
                handler.postDelayed(this, refreshIntervalMillis)
            }
        }
    init {
        handler.postDelayed(refreshRunnable, refreshIntervalMillis)
        // Observe changes in auction items LiveData
        viewModel.getAuctionItems().observeForever { items ->
            setData(items)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlindAuctionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return BlindAuctionViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlindAuctionViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData: List<BlindAuctionFragment.BlindAuctionItem>) {
        data = newData
        notifyDataSetChanged()
    }

    inner class BlindAuctionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val boxContainerTextView: TextView = itemView.findViewById(R.id.boxContainerTextView)
        private val bidPrice: TextView = itemView.findViewById(R.id.bidpriceTextView)
        private val timerTextView: TextView = itemView.findViewById(R.id.timerTextView)
        private var countDownTimer: CountDownTimer? = null

        init {
            itemView.setOnClickListener {
                if(timerTextView.text=="00:00") {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val selectedItem = data[position]
                        val context = itemView.context
                        if (context is AppCompatActivity) {
                            context.navigateToBiddingEngine(selectedItem)

                        }
                    }
                }
            }
        }

        fun bind(item: BlindAuctionFragment.BlindAuctionItem) {
            val currentTime = Calendar.getInstance().time

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val startTime = sdf.parse(item.startdate)

            val differenceInMillis = startTime.time - currentTime.time

            Log.d("DifferenceInMillis", "Difference in milliseconds: $differenceInMillis")

            if (differenceInMillis > 0) {
                itemView.isEnabled = true
                itemView.isClickable = true


                countDownTimer?.cancel()
                countDownTimer = object : CountDownTimer(differenceInMillis, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val seconds = millisUntilFinished / 1000
                        val minutes = seconds / 60
                        val hours = minutes / 60
                        val days = hours / 24
                        if(days != 0L && hours != 0L){
                            val remainingTime = String.format("%02d:%02d:%02d:%02d",
                                days, hours % 24, minutes % 60, seconds % 60)
                            timerTextView.text = remainingTime
                        } else if(hours != 0L){
                            val remainingTime = String.format("%02d:%02d:%02d",
                                 hours % 24, minutes % 60, seconds % 60)
                            timerTextView.text = remainingTime
                        } else {
                            val remainingTime = String.format("%02d:%02d",
                                minutes % 60, seconds % 60)
                            timerTextView.text = remainingTime
                        }

                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onFinish() {
                        timerTextView.text = "00:00"
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            data = data.filterIndexed { index, _ -> index != position }
                            notifyDataSetChanged()
                        }
                    }
                }.start()
            } else {
                timerTextView.text = "00:00"
            }
            

            boxContainerTextView.text = "Container: ${item.boxContainer}"
            bidPrice.text = "Bid: ${item.bidprice}"
        }
        private fun disableItem() {
            itemView.isEnabled = false
            itemView.isClickable = false
        }
    }
    fun AppCompatActivity.navigateToBiddingEngine(selectedItem: BlindAuctionFragment.BlindAuctionItem) {

        // Get user ID and username from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")
        val username = sharedPreferences.getString("username", "")

        // Realtime Database instance
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()

        // Reference to the blindbiddingengine collection
        val blindBiddingRef: DatabaseReference = database.getReference("blindbiddingengine")
        if (userId != null) {
            blindBiddingRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        // Box container doesn't exist, create it
                        val initialBidPrice = selectedItem.bidprice

                        val biddingData = hashMapOf(
                            "boxcontainer" to selectedItem.boxContainer,
                            "username" to username,
                            "bidprice" to initialBidPrice
                        )

                        // Store the bidding data in the blindbiddingengine collection
                        blindBiddingRef.child(userId).setValue(biddingData)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    navigateToBiddingEngineFragment(selectedItem)

                                } else {
                                    Log.e("BlindAuctionAdapter", "Error creating bidding data", task.exception)
                                }
                            }

                    } else {
                        // Box container exists, handle as needed
                        // Box container exists, check if it matches
                        val userBoxContainer = snapshot.child("boxcontainer").getValue(String::class.java)
                        if (userBoxContainer == selectedItem.boxContainer) {
                            // User's box container matches, navigate to bidding engine fragment
                            navigateToBiddingEngineFragment(selectedItem)
                        } else {
                            // User's box container does not match, do nothing
                            Log.d("BlindAuctionAdapter", "User's box container does not match the selected item's box container")
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                    Log.e("BlindAuctionAdapter", "Error checking box container existence", error.toException())
                }
            })
        }

    }
    private fun AppCompatActivity.navigateToBiddingEngineFragment(selectedItem: BlindAuctionFragment.BlindAuctionItem) {

        val dialogView = LayoutInflater.from(context).inflate(R.layout.disclaimer_page, null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.show()
        dialogView.findViewById<Button>(R.id.UnderstoodButton).setOnClickListener {
            dialog.dismiss() // Dismiss dialog if OK is clicked
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            val biddingEngineFragment = BiddingEngineFragment().apply {
                arguments = Bundle().apply {
                    // Pass the selected item to the fragment as arguments
                    putParcelable("selectedItem", selectedItem)
                }
            }
        transaction.replace(R.id.frame_container, biddingEngineFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        }

    }
}

