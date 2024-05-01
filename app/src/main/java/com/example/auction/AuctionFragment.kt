package com.example.auction

import android.os.Bundle
import android.content.Intent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentActivity
import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.auction.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AuctionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var uploadButton: Button
    private lateinit var documentuploadButton: Button
    private lateinit var fileTextView: TextView
    private lateinit var fileDocumentView: TextView
    private var selectedFilePath: String? = null
    private val FILE_PICKER_REQUEST_CODE = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout and return the view
        val view = inflater.inflate(R.layout.fragment_auction, container, false)

        uploadButton = view.findViewById(R.id.upload_button)
        documentuploadButton = view.findViewById(R.id.documentupload_button)
        fileTextView = view.findViewById(R.id.fileTextView)
        fileDocumentView = view.findViewById(R.id.fileDocumentView)

        uploadButton.setOnClickListener {
            launchFilePicker()
        }

        documentuploadButton.setOnClickListener {
            launchFilePicker()
        }

        return view
    }

    private fun launchFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // allows selection of various file types
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == FragmentActivity.RESULT_OK) {
            val selectedFileUri = data?.data ?: return
            selectedFilePath = selectedFileUri.path

            // Update UI to display the filename without submitting
            if (uploadButton.isPressed) {
                fileTextView.visibility = View.VISIBLE
                fileTextView.text = selectedFilePath?.substringAfterLast("/")
            }
            if (documentuploadButton.isPressed) {
                fileDocumentView.visibility = View.VISIBLE
                fileDocumentView.text = selectedFilePath?.substringAfterLast("/")
            }

            // Placeholder upload logic (replace with your implementation)
            uploadSelectedFile(selectedFilePath!!) // Assuming you have an uploadSelectedFile method
            uploaddocumentFile(selectedFilePath!!)
        }

    }
    private fun uploadSelectedFile(filePath: String) {
        // Replace with your actual upload logic using libraries like Volley or Retrofit
        // This is a placeholder for demonstration purposes

        val uploadSuccessful = true // Assuming successful upload
        var uploaddocument = true
        if (uploadSuccessful) {
            Log.d("AuctionFragment", "File uploaded successfully: $filePath")
            val downloadUrl = "https://your-server.com/download/$filePath"

            // Display download link (assuming you have a TextView for download link)
            val filename = downloadUrl.substringAfterLast("/")

            // Display filename in the TextView
            fileTextView.visibility = View.VISIBLE
            fileTextView.text = filename

            val toast = Toast.makeText(requireActivity(), "File Uploaded Successfully!", Toast.LENGTH_SHORT)
            toast.show()
        }
        if (uploadSuccessful) {
            Log.d("AuctionFragment", "File uploaded successfully: $filePath")
            val downloadUrl = "https://your-server.com/download/$filePath"

            // Display download link (assuming you have a TextView for download link)
            val filename = downloadUrl.substringAfterLast("/")

            // Display filename in the TextView
            fileTextView.visibility = View.VISIBLE
            fileTextView.text = filename

            val toast = Toast.makeText(requireActivity(), "File Uploaded Successfully!", Toast.LENGTH_SHORT)
            toast.show()
        }else {
            Log.e("AuctionFragment", "File upload failed!")
        }
    }
    private fun uploaddocumentFile(filePath: String) {
        // Replace with your actual upload logic using libraries like Volley or Retrofit
        // This is a placeholder for demonstration purposes

        val uploadSuccessful = true // Assuming successful upload (replace with actual logic)
        if (uploadSuccessful) {
            Log.d("AuctionFragment", "File uploaded successfully: $filePath")
            val downloadUrl = "https://your-server.com/download/$filePath"

            // Display download link (assuming you have a TextView for download link)
            val filename = downloadUrl.substringAfterLast("/")

            // Display filename in the TextView
            fileDocumentView.visibility = View.VISIBLE
            fileDocumentView.text = filename

            val toast = Toast.makeText(requireActivity(), "File Uploaded Successfully!", Toast.LENGTH_SHORT)
            toast.show()
        } else {
            Log.e("AuctionFragment", "File upload failed!")
        }
    }




    companion object {
        val IMAGE_REQUEST_CODE = 100
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AuctionFragment.
         */
        // TODO: Rename and change types and number of parameters


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AuctionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
