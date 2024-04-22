package com.example.auction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AuctionItemAdapter(private var bids: List<BiddingEngineFragment.Bid>) :
    RecyclerView.Adapter<AuctionItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val bidpriceTextView: TextView = itemView.findViewById(R.id.bidpriceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.blind_itemview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bid = bids[position]
        holder.usernameTextView.text = bid.username
        holder.bidpriceTextView.text = bid.price.toString()
    }

    override fun getItemCount(): Int {
        return bids.size
    }
    fun updateData(newBids: List<BiddingEngineFragment.Bid>) {
        bids = newBids
        notifyDataSetChanged()
    }
}

