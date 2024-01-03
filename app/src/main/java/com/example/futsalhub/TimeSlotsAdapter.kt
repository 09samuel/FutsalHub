package com.example.futsalhub

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class TimeSlotsAdapter(private val context: Context, private val timeSlotsList: List<String>, private val priceList: List<String>) : BaseAdapter() {
    private var onItemClick: ((timeSlot: String, price: String) -> Unit)? = null
    fun setOnItemClickListener(listener: (timeSlot: String, price: String) -> Unit) {
        this.onItemClick = listener
    }

    override fun getCount(): Int {
        return timeSlotsList.size
    }

    override fun getItem(position: Int): Any {
        return timeSlotsList[position]

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var itemView = convertView
        val viewHolder: ViewHolder

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.time_slot_item, parent, false)
            viewHolder = ViewHolder()
            viewHolder.timeSlotTextView = itemView.findViewById(R.id.tvTime)
            viewHolder.priceTextView = itemView.findViewById(R.id.tvPrice)

            itemView.tag = viewHolder
        } else {
            viewHolder = itemView.tag as ViewHolder
        }

        val timeSlot = timeSlotsList[position]
        val price = priceList[position]

        viewHolder.timeSlotTextView?.text = timeSlot
        viewHolder.priceTextView?.text = "₹"+price

        itemView?.setOnClickListener {
            onItemClick?.invoke(timeSlot, price)
        }

        return itemView!!
    }


    private class ViewHolder {
        var timeSlotTextView: TextView? = null
        var priceTextView: TextView? = null
    }
}
