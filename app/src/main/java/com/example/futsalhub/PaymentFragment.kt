package com.example.futsalhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.futsalhub.databinding.FragmentPaymentBinding

class PaymentFragment : Fragment() {
    private lateinit var binding: FragmentPaymentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)

        setFragmentResultListener("r") { _, bundle ->
            var bookingTime = bundle.getString("time").toString()
            var bookingPrice = bundle.getString("price").toString()
            var groundId = bundle.getString("groundId").toString()
            var bookingDate = bundle.getString("date").toString()
        }
        return binding.root
    }
}