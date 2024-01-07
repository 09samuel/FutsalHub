package com.example.futsalhub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.futsalhub.databinding.FragmentRemoveGroundBinding
import com.google.firebase.firestore.FirebaseFirestore


class RemoveGroundFragment : Fragment() {
    private lateinit var binding: FragmentRemoveGroundBinding
    private var dataList: MutableList<HashMap<String, Any?>> = mutableListOf()
    private lateinit var adapter: RemoveAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRemoveGroundBinding.inflate(inflater, container, false)


        // Set up RecyclerView
        val layoutManager = LinearLayoutManager(context)
        val recyclerView = binding.rvRemoveGround
        recyclerView.layoutManager = layoutManager

        FirebaseFirestore.getInstance()
            .collection("FutsalGrounds")
            .orderBy("groundName").get().addOnSuccessListener { documents ->
                for (document in documents) {

                    val newData = HashMap<String, Any?>()
                    newData.putAll(document.data)
                    dataList.add(newData)

                }
                adapter = RemoveAdapter(dataList)
                recyclerView.adapter = adapter

                adapter.setOnItemClickListener(object : RemoveAdapter.OnItemClickListener {
                    override fun onClick(position: Int, groundId: String) {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Remove Ground")
                        builder.setMessage("Are you sure you want to remove this ground?")
                        builder.setPositiveButton("Confirm") { _, _ ->
                            dataList.removeAt(position)
                            adapter.notifyItemRemoved(position)

                            FirebaseFirestore.getInstance().collection("FutsalGrounds").document(groundId).delete()
                                .addOnSuccessListener {
                                    Log.i("removeGround", "success")
                                }.addOnFailureListener { e ->
                                    Log.i("removeGround", e.toString())
                                }
                        }
                        builder.setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        val dialog = builder.create()
                        dialog.show()                    }
                })
            }.addOnFailureListener { exception ->
                Log.i("oi21345", exception.toString())
            }
        return binding.root
    }

}