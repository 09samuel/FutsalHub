package com.example.futsalhub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.futsalhub.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Profile"

        binding.tvLogOut.setOnClickListener {
            firebaseAuth = FirebaseAuth.getInstance()

            firebaseAuth.signOut()
            val intent = Intent(activity, StartActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        return binding.root
    }


}

