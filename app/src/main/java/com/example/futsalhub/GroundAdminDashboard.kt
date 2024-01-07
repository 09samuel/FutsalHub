package com.example.futsalhub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.futsalhub.databinding.FragmentGroundAdminDashboardBinding
import com.google.firebase.auth.FirebaseAuth


class GroundAdminDashboard : Fragment() {
    private lateinit var binding: FragmentGroundAdminDashboardBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroundAdminDashboardBinding.inflate(inflater, container, false)

        binding.button2.setOnClickListener {
            findNavController().navigate(R.id.action_groundAdminDashboard_to_selectGroundFragment)
        }

        binding.button3.setOnClickListener {
            val firebaseAuth = FirebaseAuth.getInstance()

            firebaseAuth.signOut()
            val intent = Intent(activity, StartActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        return binding.root
    }


}