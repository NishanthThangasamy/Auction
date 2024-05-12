package com.example.auction

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID


class AuctionFragment : Fragment() {
    private lateinit var categorySpinner: Spinner
    private lateinit var typeEditText: EditText
    private lateinit var brandEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var btnUploadImage: Button
    private lateinit var btnUploadDocument: Button
    private lateinit var tvFileNames: TextView
    private lateinit var tvFileName: TextView
    private lateinit var submitButton: Button

    private lateinit var storageRef: StorageReference
    private lateinit var firestore: FirebaseFirestore

    private var imageUri: Uri? = null
    private var documentUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_auction, container, false)

        categorySpinner = view.findViewById(R.id.spinnerCategory)
        typeEditText = view.findViewById(R.id.editTexttype)
        brandEditText = view.findViewById(R.id.editTextBrand)
        nameEditText = view.findViewById(R.id.editTextName)
        descriptionEditText = view.findViewById(R.id.addressEditText)
        priceEditText = view.findViewById(R.id.editTextstartprice)
        btnUploadImage = view.findViewById(R.id.btnUploadImage)
        btnUploadDocument = view.findViewById(R.id.btnUploadDocument)
        tvFileNames = view.findViewById(R.id.tvFileNames)
        tvFileName = view.findViewById(R.id.tvdocumentname)
        submitButton = view.findViewById(R.id.submit_button)

        storageRef = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        btnUploadImage.setOnClickListener {
            openFileManager(IMAGE_REQUEST_CODE)
        }

        btnUploadDocument.setOnClickListener {
            openFileManager(DOCUMENT_REQUEST_CODE)
        }

        submitButton.setOnClickListener {
            uploadFiles()
        }

        return view
    }

    private fun openFileManager(requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, requestCode)
    }

    private fun uploadFiles() {
        val category = categorySpinner.selectedItem.toString()
        val type = typeEditText.text.toString()
        val brand = brandEditText.text.toString()
        val name = nameEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val price = priceEditText.text.toString().toDoubleOrNull()

        if (imageUri != null && documentUri != null && !type.isBlank() && !brand.isBlank() && !name.isBlank() && !description.isBlank() && price != null) {
            uploadImage()
        } else {
            Toast.makeText(context, "Please fill in all fields and upload both image and document", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage() {
        // Create a reference to the location where the image will be stored in Firebase Storage
        val imageRef = storageRef.child("images/${UUID.randomUUID()}")

        // Upload the image file to Firebase Storage
        imageRef.putFile(imageUri!!)
            .addOnSuccessListener { uploadTaskSnapshot ->
                // If the image upload is successful, retrieve the download URL of the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { imageDownloadUrl ->
                    // Once the download URL is retrieved, proceed to upload the document
                    uploadDocument(imageDownloadUrl.toString())
                }.addOnFailureListener {
                    // If failed to retrieve the image download URL, show an error message
                    Toast.makeText(context, "Failed to get image download URL", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // If the image upload fails, show an error message
                Toast.makeText(context, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun uploadDocument(imageUrl: String) {
        val documentRef = storageRef.child("documents/${UUID.randomUUID()}")
        documentRef.putFile(documentUri!!)
            .addOnSuccessListener {
                documentRef.downloadUrl.addOnSuccessListener { documentDownloadUrl ->
                    saveData(imageUrl, documentDownloadUrl.toString())
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to get document download URL", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload document", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveData(imageUrl: String, documentUrl: String) {
        val auctionData = hashMapOf(
            "category" to categorySpinner.selectedItem.toString(),
            "type" to typeEditText.text.toString(),
            "brand" to brandEditText.text.toString(),
            "name" to nameEditText.text.toString(),
            "description" to descriptionEditText.text.toString(),
            "price" to priceEditText.text.toString().toDoubleOrNull(),
            "imageUrl" to imageUrl,
            "documentUrl" to documentUrl
        )
        FirebaseDatabase.getInstance().getReference("auctions")
            .push()
            .setValue(auctionData)
            .addOnSuccessListener {
                Toast.makeText(context, "Data stored successfully", Toast.LENGTH_SHORT).show()
                // Clear input fields and reset UI as needed
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to store data", Toast.LENGTH_SHORT).show()
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGE_REQUEST_CODE -> {
                    imageUri = data?.data
                    val fileName = imageUri?.lastPathSegment
                    fileName?.let {
                        tvFileNames.append("Image: $fileName\n")
                    }
                }
                DOCUMENT_REQUEST_CODE -> {
                    documentUri = data?.data
                    val fileName = documentUri?.lastPathSegment
                    fileName?.let {
                        tvFileName.append("Document: $fileName\n")
                    }
                }
            }
        }
    }

    companion object {
        private const val IMAGE_REQUEST_CODE = 100
        private const val DOCUMENT_REQUEST_CODE = 200
    }
}


