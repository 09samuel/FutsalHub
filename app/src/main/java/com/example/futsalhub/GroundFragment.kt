package com.example.futsalhub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class GroundFragment : Fragment() {
    //private lateinit var binding: FragmentGroundBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // binding = FragmentGroundBinding.inflate(inflater,container,false)


        val str= arguments?.getString("str")

        if (str != null) {
            Log.e("mytag", str)
        }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ground, container, false)

        //return binding.root
    }

}