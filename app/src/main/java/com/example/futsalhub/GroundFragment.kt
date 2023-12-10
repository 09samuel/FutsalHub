package com.example.futsalhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.futsalhub.databinding.FragmentGroundBinding


class GroundFragment : Fragment() {
    private lateinit var binding: FragmentGroundBinding
    private lateinit var groundId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroundBinding.inflate(inflater,container,false)

        (activity as AppCompatActivity).supportActionBar?.title = "Book your ground"

        setFragmentResultListener("requestKey") { requestKey, bundle ->
            groundId = bundle.getString("bundleKey").toString()
        }

        return binding.root
    }

}