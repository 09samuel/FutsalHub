package com.example.futsalhub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.futsalhub.databinding.FragmentGroundBinding
import com.google.firebase.firestore.FirebaseFirestore


class GroundFragment : Fragment() {
    private lateinit var binding: FragmentGroundBinding
    private lateinit var groundId: String
    private lateinit var db :  FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):

            View? {
        binding = FragmentGroundBinding.inflate(inflater,container,false)

        (activity as AppCompatActivity).supportActionBar?.title = "Book your ground"

        setFragmentResultListener("requestKey") { requestKey, bundle ->
            groundId = bundle.getString("bundleKey").toString()
            db = FirebaseFirestore.getInstance()
            val ref=db.collection("FutsalGrounds").document(groundId)
            ref.get().addOnSuccessListener {
                if (it!=null){
                    binding.tvDesc.text=it.data?.get("groundName").toString()
                }
            }
                .addOnFailureListener {
                    Log.i("mytag","fail")
                }
        }

        return binding.root
    }

}