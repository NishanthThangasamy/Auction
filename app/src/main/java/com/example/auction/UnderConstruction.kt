package com.example.auction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class UnderConstruction : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var userId: String
    private lateinit var boxContainer: String
    private lateinit var price: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString("userId", "")
            boxContainer = it.getString("boxContainer", "")
            price = it.getString("bidRate","")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_under_construction, container, false)

        val UserId = view.findViewById<TextView>(R.id.UserIdTextView)
        val boxN0 = view.findViewById<TextView>(R.id.BoxNoTextView)
        val Price = view.findViewById<TextView>(R.id.PriceTextView)

        UserId.text = userId
        boxN0.text = boxContainer
        Price.text = price

        return view
    }
}