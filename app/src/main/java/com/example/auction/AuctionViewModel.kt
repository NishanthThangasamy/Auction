package com.example.auction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuctionViewModel : ViewModel() {
    private val auctionItems = MutableLiveData<List<BlindAuctionFragment.BlindAuctionItem>>()

    fun getAuctionItems(): LiveData<List<BlindAuctionFragment.BlindAuctionItem>> {
        // Here you would typically fetch data from your database and update auctionItems
        // For now, I'm setting it to an empty list
//        auctionItems.value = emptyList()
        return auctionItems
    }

    // Method to update auction items
    fun updateAuctionItems(newItems: List<BlindAuctionFragment.BlindAuctionItem>) {
        auctionItems.value = newItems
    }
}
