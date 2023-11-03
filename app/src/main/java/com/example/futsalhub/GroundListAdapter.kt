package com.example.futsalhub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class GroundListAdapter(options: FirestoreRecyclerOptions<GroundListModel>) :
    FirestoreRecyclerAdapter<GroundListModel, GroundListAdapter.GroundAdapterVH>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroundAdapterVH {
        return GroundAdapterVH(LayoutInflater.from(parent.context).inflate(R.layout.ground_item, parent, false))
    }

    override fun onBindViewHolder(holder: GroundAdapterVH, position: Int, model: GroundListModel) {
        holder.groundName.text = model.groundName
        holder.location.text = model.location
        holder.minPrice.text = "₹"+model.minPrice
        holder.ovrRating.text = model.ovrRating
    }

    class GroundAdapterVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        val groundName: TextView = itemView.findViewById(R.id.tvGroundName)
        val location: TextView = itemView.findViewById(R.id.tvGroundLocation)
        val minPrice: TextView = itemView.findViewById(R.id.tvGroundPrice)
        val ovrRating: TextView = itemView.findViewById(R.id.tvGroundRating)
    }
}