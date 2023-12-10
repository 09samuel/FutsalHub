package com.example.futsalhub

import android.app.SearchManager
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futsalhub.databinding.FragmentHomeBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class HomeFragment : Fragment() {

    var groundAdapter: GroundListAdapter? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)

        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.futsalhub_logo)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = ""
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.green)))

        val query: Query = FirebaseFirestore.getInstance()
            .collection("FutsalGrounds")
            .orderBy("ovrRating")

        val firestoreRecyclerOptions: FirestoreRecyclerOptions<GroundListModel> =
            FirestoreRecyclerOptions.Builder<GroundListModel>()
                .setQuery(query, GroundListModel::class.java)
                .build()

        val layoutManager = LinearLayoutManager(context)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = layoutManager
        groundAdapter = GroundListAdapter(firestoreRecyclerOptions, ::handleUserData)

        recyclerView.adapter = groundAdapter


        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchBar.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))

        binding.searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                val q: Query = FirebaseFirestore.getInstance()
                    .collection("FutsalGrounds")
                    .orderBy("groundName").startAt(query?.capitalizeWords()).endAt(query?.capitalizeWords() + '~');

                val firestoreRecyclerOptions: FirestoreRecyclerOptions<GroundListModel> =
                    FirestoreRecyclerOptions.Builder<GroundListModel>()
                        .setQuery(q, GroundListModel::class.java)
                        .build()

                val layoutManager = LinearLayoutManager(context)
                recyclerView = binding.recyclerView
                recyclerView.layoutManager = layoutManager
                groundAdapter = GroundListAdapter(firestoreRecyclerOptions, ::handleUserData)
                groundAdapter!!.startListening()
                recyclerView.adapter = groundAdapter

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               val q: Query = FirebaseFirestore.getInstance()
                    .collection("FutsalGrounds")
                    .orderBy("groundName").startAt(newText?.capitalizeWords()).endAt(newText?.capitalizeWords() + '~');

                val firestoreRecyclerOptions: FirestoreRecyclerOptions<GroundListModel> =
                    FirestoreRecyclerOptions.Builder<GroundListModel>()
                        .setQuery(q, GroundListModel::class.java)
                        .build()

                val layoutManager = LinearLayoutManager(context)
                recyclerView = binding.recyclerView
                recyclerView.layoutManager = layoutManager
                groundAdapter = GroundListAdapter(firestoreRecyclerOptions, ::handleUserData)
                groundAdapter!!.startListening()
                recyclerView.adapter = groundAdapter
                return false
            }
        })

        binding.btnSortFilter.setOnClickListener {
            val bottomSheetFragment = ModalBottomSheet()
            bottomSheetFragment.show(requireActivity().supportFragmentManager, "BSDialogFragment")
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")

    private fun handleUserData(data: GroundListModel) {
        setFragmentResult("requestKey", bundleOf("bundleKey" to data.groundId))
        findNavController().navigate(R.id.action_listScreen_to_groundScreen)
    }

    override fun onStart() {
        super.onStart()
        groundAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        groundAdapter?.stopListening()
    }
}