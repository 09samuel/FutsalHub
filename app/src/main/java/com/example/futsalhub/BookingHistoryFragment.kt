package com.example.futsalhub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.futsalhub.databinding.FragmentBookingHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class BookingHistoryFragment : Fragment() {
    lateinit var binding: FragmentBookingHistoryBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var db : FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBookingHistoryBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Booking History"


        db = FirebaseFirestore.getInstance()
        firebaseAuth=FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid
        Log.i("booking123", userId.toString())

        db.collection("Bookings")
            .whereEqualTo("userId", userId)
            .orderBy("bookingDate", Query.Direction.DESCENDING)
            .orderBy("bookingTime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Handle each document here
                    val data = document.data
                    // Access the fields within the document
                    val bookingDate = data["bookingDate"]


                    // Handle the retrieved data accordingly
                }
            }.addOnFailureListener { exception ->


                // Handle any errors that occurred

                Log.i("booking123", "Error getting documents: ", exception)
            }

        return binding.root
    }
}


