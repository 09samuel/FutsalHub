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
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
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

        setFragmentResultListener("requestKey") { requestKey, bundle ->
            groundId = bundle.getString("bundleKey").toString()

            db = FirebaseFirestore.getInstance()
            val ref = db.collection("FutsalGrounds").document(groundId)
            ref.get().addOnSuccessListener {
                if (it != null) {
                    binding.tvDesc.text = it.data?.get("description").toString()
                    binding.tvOvr.text = it.data?.get("ovrRating").toString()
                    binding.tvGround.text = it.data?.get("groundName").toString()
                    binding.tvLocation.text = it.data?.get("location").toString()
                    binding.tvPrice.text = "₹" + it.data?.get("minPrice").toString()
                }
            }

            class DayViewContainer(view: View) : ViewContainer(view) {
                val bind = CalendarBinding.bind(view)
                lateinit var day: WeekDay

                init {
                    view.setOnClickListener {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            binding.exSevenCalendar.notifyDateChanged(day.date)
                            oldDate?.let { binding.exSevenCalendar.notifyDateChanged(it) }
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
                override fun bind(container: DayViewContainer, data: WeekDay) = container.bind(data)
            }

            /*binding.exSevenCalendar.weekScrollListener = { weekDays ->
                binding.exSevenToolbar.title = getWeekPageTitle(weekDays)
            }*/

            //val currentMonth = YearMonth.now()
            val currentDate = LocalDate.now()
            binding.exSevenCalendar.setup(
                currentDate,
                currentDate.plusDays(6),
                firstDayOfWeekFromLocale(),
            )
            binding.exSevenCalendar.scrollToDate(LocalDate.now())
        }

        return binding.root
    }

}