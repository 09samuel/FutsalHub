package com.example.futsalhub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futsalhub.databinding.FragmentBookingHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class BookingHistoryFragment : Fragment() {
    lateinit var binding: FragmentBookingHistoryBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookingHistoryAdapter
    private var dataList: MutableList<HashMap<String, Any?>> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBookingHistoryBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Booking History"


        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid
        Log.i("booking123", userId.toString())

        db.collection("Bookings").whereEqualTo("customerId", userId)
            .orderBy("bookingDate", Query.Direction.DESCENDING)
            .orderBy("bookingTime", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val newData = HashMap<String, Any?>()

                    // Handle each document here
                    val data = document.data
                    // Access the fields within the document
                    newData.putAll(data)

                    dataList.add(newData)
                    Log.i("booking123", newData.toString())

                }
                //Log.i("booking123", dataList.toString())
                val layoutManager = LinearLayoutManager(context)
                recyclerView = binding.rvHistory
                recyclerView.layoutManager = layoutManager

                adapter = BookingHistoryAdapter(dataList)
                recyclerView.adapter = adapter

                adapter.setOnItemClickListener(object : BookingHistoryAdapter.OnItemClickListener {
                    override fun onCancelBookingClick(position: Int, bookingId: String) {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Cancel Booking")
                        builder.setMessage("Are you sure you want to cancel this booking?")
                        builder.setPositiveButton("Confirm") { _, _ ->
                            cancelBooking(position)

                            db.collection("Bookings").document(bookingId).delete()
                                .addOnSuccessListener {
                                    Log.i("cancelBook", "success")
                                }.addOnFailureListener { e ->
                                    Log.i("cancelBook", e.toString())
                                }
                        }
                        builder.setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        val dialog = builder.create()
                        dialog.show()


                    }

                    override fun onRatingBookingClick(
                        position: Int, rating: Float, groundId: String, bookingId: String
                    ) {
                        db.collection("Bookings").document(bookingId)
                            .update("bookingRating", rating.toString()).addOnSuccessListener {
                                Log.i("rate", "success")

                            }.addOnFailureListener {
                                Log.i("rate", it.toString())
                            }

                        val docref = db.collection("FutsalGrounds").document(groundId)
                        docref.get().addOnSuccessListener { document ->
                            val fieldValue = document.get("ovrRating").toString()
                            val count = document.get("ovrRatingCount").toString()
                            val countrat = count.toFloat()

                            val newRating = (fieldValue.toFloat() + rating) / countrat

                            val updates = hashMapOf<String, Any>(
                                "ovrRating" to newRating.toString(),
                                "ovrRatingCount" to (countrat + 1).toString()
                            )
                            docref.update(updates).addOnSuccessListener {
                                Log.i("rate1", "success")
                            }.addOnFailureListener {
                                Log.i("rate1", it.toString())
                            }

                        }.addOnFailureListener {
                            Log.i("rate1", it.toString())

                        }
                    }
                })


            }.addOnFailureListener { exception ->
                Log.i("booking123", "Error getting documents: ", exception)
            }

        return binding.root
    }

    private fun cancelBooking(position: Int) {
        dataList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

}


