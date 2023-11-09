package com.example.futsalhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class HomeFragment : Fragment() {

    var groundAdapter: GroundListAdapter? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query: Query = FirebaseFirestore.getInstance()
            .collection("FutsalGrounds")
            .orderBy("groundName")

        val firestoreRecyclerOptions: FirestoreRecyclerOptions<GroundListModel> =
            FirestoreRecyclerOptions.Builder<GroundListModel>()
                .setQuery(query, GroundListModel::class.java)
                .build()

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = layoutManager
        groundAdapter = GroundListAdapter(firestoreRecyclerOptions)
        recyclerView.adapter = groundAdapter


        groundAdapter!!.onItemClick = {
            view.findNavController().navigate(R.id.action_listScreen_to_groundScreen)
        }


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