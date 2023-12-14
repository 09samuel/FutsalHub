package com.example.futsalhub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.futsalhub.databinding.FragmentOverallAdminDashboardBinding
import com.google.firebase.auth.FirebaseAuth


class OverallAdminDashboard : Fragment() {
    private lateinit var binding: FragmentOverallAdminDashboardBinding
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentOverallAdminDashboardBinding.inflate(inflater,container,false)
        binding.button.setOnClickListener {
            firebaseAuth = FirebaseAuth.getInstance()

            firebaseAuth.signOut()
            val intent = Intent(activity, StartActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return binding.root
    }

}