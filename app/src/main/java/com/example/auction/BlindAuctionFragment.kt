package com.example.auction

import android.os.Handler
import android.os.Looper
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class BlindAuctionFragment : Fragment() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BlindAuctionAdapter
    private lateinit var viewModel: AuctionViewModel

    private val refreshIntervalMillis = 1000L // 1 second
    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            fetchBlindAuctionData()
            handler.postDelayed(this, refreshIntervalMillis)
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_blind_auction, container, false)


        databaseReference = FirebaseDatabase.getInstance().reference
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(),1)

        viewModel = ViewModelProvider(this)[AuctionViewModel::class.java]

        // Create adapter and set it to the RecyclerView
        adapter = BlindAuctionAdapter(requireContext(),viewModel)
        recyclerView.adapter = adapter


//        // Fetch data from Firebase
//        fetchBlindAuctionData()

        // Start periodic refreshing
        handler.postDelayed(refreshRunnable, refreshIntervalMillis)

        return view
    }

    override fun onDestroyView() {
        // Stop refreshing when the view is destroyed
        handler.removeCallbacks(refreshRunnable)
        super.onDestroyView()
    }


    private fun fetchBlindAuctionData() {
        val blindAuctionRef = databaseReference.child("blindauction")
        blindAuctionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("BlindAuctionFragment", "onDataChange: $snapshot")
                val blindAuctionList = mutableListOf<BlindAuctionItem>()

                for (auctionSnapshot in snapshot.children) {
                    val boxContainer = auctionSnapshot.child("boxcontainer").getValue(String::class.java)
                    val price = auctionSnapshot.child("bidprice").getValue(Long::class.java)
                    val startTime = auctionSnapshot.child("startdate").getValue(String::class.java)
                    // Add more fields as needed

                    boxContainer?.let {
                        val item = BlindAuctionItem(boxContainer,price ?: 0, startTime)
                        blindAuctionList.add(item)
                    }
                }

                // Update data set in the adapter
                viewModel.updateAuctionItems(blindAuctionList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Define other functions and classes
    data class BlindAuctionItem(
        val boxContainer: String,
        val bidprice: Long,
        val startdate: String?
        // Add more fields as needed
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readLong(),
            parcel.readString()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(boxContainer)
            parcel.writeLong(bidprice)
            parcel.writeString(startdate)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<BlindAuctionItem> {
            override fun createFromParcel(parcel: Parcel): BlindAuctionItem {
                return BlindAuctionItem(parcel)
            }

            override fun newArray(size: Int): Array<BlindAuctionItem?> {
                return arrayOfNulls(size)
            }
        }
    }
}