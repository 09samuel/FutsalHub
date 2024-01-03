package com.example.futsalhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.futsalhub.databinding.CalendarBinding
import com.example.futsalhub.databinding.FragmentGroundBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


class GroundFragment : Fragment() {
    private lateinit var binding: FragmentGroundBinding
    private lateinit var groundId: String
    private lateinit var db: FirebaseFirestore
    private var selectedDate = LocalDate.now()
    private val dateFormatter = DateTimeFormatter.ofPattern("dd")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):

            View? {
        binding = FragmentGroundBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Book your ground"



        setFragmentResultListener("requestKey") { _, bundle ->
            groundId = bundle.getString("bundleKey").toString()

            db = FirebaseFirestore.getInstance()
            val ref = db.collection("FutsalGrounds").document(groundId)
            ref.get().addOnSuccessListener {document->
                val timeSlotsList: Map<String, Any>
                val finalTimeList = mutableListOf<String>()
                val priceList:  Map<String, Any>
                val finalPriceList= mutableListOf<String>()

                if (document != null) {
                    binding.tvDesc.text = document.data?.get("description").toString()
                    binding.tvOvr.text = document.data?.get("ovrRating").toString()
                    binding.tvGround.text = document.data?.get("groundName").toString()
                    binding.tvLocation.text = document.data?.get("location").toString()
                    binding.tvPrice.text = "₹" + document.data?.get("minPrice").toString()

                    timeSlotsList = document.data?.get("timeSlots") as Map<String, Any>
                    timeSlotsList?.let {
                        // Iterate through the map and add values to the list
                        for ((_, value) in it) {
                            if (value is String) {
                                finalTimeList.add(value)
                            }
                        }
                    }
                    finalTimeList.sort()

                    //document.data["slots"] as? Map<String, Any>
                    priceList = document.data?.get("price") as Map<String, Any>
                    priceList?.let {
                        // Iterate through the map and add values to the list
                        for ((_, value) in it) {
                            if (value is String) {
                                finalPriceList.add(value)
                            }
                        }
                    }
                    finalPriceList.sort()

                    val gridView = binding.gvTimeSlots
                    val adapter = TimeSlotsAdapter(requireContext(),finalTimeList,finalPriceList)
                    gridView.adapter = adapter

                    adapter.setOnItemClickListener { timeSlot, _ ->
                        //db.collection("FutsalGrounds/")
                        enableBookButton()

                        // Handle item click here
                        // You have access to the clicked time slot and price
                    }
                }
            }

            class DayViewContainer(view: View) : ViewContainer(view) {
                val bind = CalendarBinding.bind(view)
                lateinit var day: WeekDay

                init {
                    view.setOnClickListener {
                        if(bind.exSevenDayText.alpha.toInt() ==1){
                            if (selectedDate != day.date) {
                                val oldDate = selectedDate
                                selectedDate = day.date
                                binding.exSevenCalendar.notifyDateChanged(day.date)
                                oldDate?.let { binding.exSevenCalendar.notifyDateChanged(it) }
                            }
                        }
                    }
                }

                fun bind(day: WeekDay) {
                    this.day = day
                    bind.exSevenDateText.text = dateFormatter.format(day.date)
                    bind.exSevenDayText.text = day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

                    val colorRes = if (day.date == selectedDate) {
                        R.color.black
                    } else {
                        R.color.green
                    }
                    bind.exSevenDateText.setTextColor(view.context.getColor(colorRes))
                    bind.exSevenDayText.setTextColor(view.context.getColor(colorRes))
                    bind.exSevenSelectedView.isVisible = day.date == selectedDate
                }
            }

            binding.exSevenCalendar.dayBinder = object : WeekDayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, data: WeekDay){
                    container.bind(data)
                    container.bind.exSevenDayText.alpha=if(LocalDate.now()<= data.date && LocalDate.now().plusDays(2)>=data.date) 1f else 0.3f
                    container.bind.exSevenDateText.alpha=if(LocalDate.now()<= data.date && LocalDate.now().plusDays(2)>=data.date) 1f else 0.3f
                }
            }

            val currentDate = LocalDate.now()
            binding.exSevenCalendar.setup(
                currentDate.minusDays(2),
                currentDate.plusDays(4),
                currentDate.minusDays(2).dayOfWeek,
            )
            binding.exSevenCalendar.scrollToDate(LocalDate.now())
        }

        return binding.root
    }

    private fun enableBookButton() {
        binding.btnBook.isClickable=true
        binding.btnBook.alpha=1.0f
    }

}