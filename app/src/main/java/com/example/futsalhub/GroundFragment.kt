package com.example.futsalhub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.example.futsalhub.databinding.CalendarBinding
import com.example.futsalhub.databinding.FragmentGroundBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


class GroundFragment : Fragment(), PaymentResultListener {
    private lateinit var binding: FragmentGroundBinding
    private lateinit var groundId: String
    private lateinit var db: FirebaseFirestore
    private var selectedDate = LocalDate.now()
    private val dateFormatter = DateTimeFormatter.ofPattern("dd")
    val dateFormatterInd = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH)
    private lateinit var bookingDate: String
    private lateinit var bookingPrice: String
    private lateinit var bookingTime: String
    private lateinit var groundName: String
    private lateinit var ImgView: ImageView
    private lateinit var gimg: String
    private lateinit var storage: FirebaseStorage


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):

            View? {
        binding = FragmentGroundBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Book your ground"

        setFragmentResultListener("requestKey") { _, bundle ->
            groundId = bundle.getString("groundId").toString()

            FirebaseStorage.getInstance()
            db = FirebaseFirestore.getInstance()
            val ref = db.collection("FutsalGrounds").document(groundId)
            ref.get().addOnSuccessListener { document ->
                if (document != null) {
                    binding.tvDesc.text = document.data?.get("description").toString()
                    binding.tvOvr.text = document.data?.get("ovrRating").toString()
                    binding.tvGround.text = document.data?.get("groundName").toString()
                    binding.tvLocation.text = document.data?.get("location").toString()
                    groundName = document.data?.get("groundName").toString()
                    ImgView = binding.imageView
                    gimg = document.data?.get("groundImg").toString()
                    Glide.with(this).load(gimg).into(ImgView)
                }
            }

            class DayViewContainer(view: View) : ViewContainer(view) {
                val bind = CalendarBinding.bind(view)
                lateinit var day: WeekDay

                init {
                    view.setOnClickListener {
                        if (bind.exSevenDayText.alpha.toInt() == 1) {
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
                    bind.exSevenDayText.text =
                        day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

                    val colorRes = if (day.date == selectedDate) {
                        R.color.black
                    } else {
                        R.color.green
                    }
                    bind.exSevenDateText.setTextColor(view.context.getColor(colorRes))
                    bind.exSevenDayText.setTextColor(view.context.getColor(colorRes))
                    bind.exSevenSelectedView.isVisible = day.date == selectedDate

                    if (bind.exSevenSelectedView.isVisible) {
                        bookingDate = day.date.format(dateFormatterInd)


                        db.collection("FutsalGrounds").document(groundId).collection("TImeSlots")
                            .document(bookingDate).get().addOnSuccessListener { document ->
                                val timeData = HashMap<String, Any?>()
                                val data = document.data

                                // Add data to the new data map
                                if (data != null) {
                                    timeData.putAll(data)
                                }

                                Log.i("time123", timeData.toString())
                                val gridView = binding.gvTimeSlots
                                val adapter =
                                    TimeSlotsAdapter(requireContext(), timeData.toSortedMap())
                                gridView.adapter = adapter

                                //disableBookButton()

                                adapter.setOnItemClickListener { timeSlot, price ->
                                    enableBookButton()
                                    binding.tvPrice.text = "₹$price"
                                }
                            }
                    }
                }
            }

            binding.exSevenCalendar.dayBinder = object : WeekDayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, data: WeekDay) {
                    container.bind(data)
                    container.bind.exSevenDayText.alpha =
                        if (LocalDate.now() <= data.date && LocalDate.now()
                                .plusDays(2) >= data.date
                        ) 1f else 0.3f
                    container.bind.exSevenDateText.alpha =
                        if (LocalDate.now() <= data.date && LocalDate.now()
                                .plusDays(2) >= data.date
                        ) 1f else 0.3f
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

        binding.btnBook.setOnClickListener {
            /*setFragmentResult(
                "r",
                bundleOf(
                    "time" to bookingTime,
                    "price" to bookingPrice,
                    "date" to bookingDate,
                    "groundId" to groundId,
                    "groundName" to groundName
                )
            )*/
            savePayment(1000)
            //findNavController().navigate(R.id.action_groundScreen_to_paymentFragment)
        }


        Checkout.preload(requireActivity())

        return binding.root
    }

    private fun enableBookButton() {
        binding.btnBook.isClickable = true
        binding.btnBook.alpha = 1.0f
    }

    private fun disableBookButton() {
        binding.btnBook.isClickable = true
        binding.btnBook.alpha = 0.5f
    }

    private fun savePayment(bookingPrice: Int) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_bTaaURb4EQKwlB")
        try {
            val options = JSONObject()
            options.put("name", "FutsalHub")
            options.put("description", "FutsalHub")
            options.put("theme.color", "#1ED660")
            options.put("theme.backdrop_color", "#121212")
            options.put("currency", "INR")
            options.put("amount", bookingPrice * 100)

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)

            /*val prefill = JSONObject()
            prefill.put("email","gaurav.kumar@example.com")

            options.put("prefill",prefill)*/

            checkout.open(requireActivity(), options)

            /*val payoutRequest = JSONObject()
            payoutRequest.put("account_number", "merchant_account_number")
            payoutRequest.put("amount", 1000) // Amount in paise
            payoutRequest.put("currency", "INR")
            payoutRequest.put("mode", "UPI")
            payoutRequest.put("purpose", "payout")
            payoutRequest.put("queue_if_low_balance", true)

            val payout = razorpay.Payouts.create(payoutRequest)*/
        } catch (e: Exception) {
            Log.i("exception123",e.toString())

        }

    }

    override fun onPaymentSuccess(p0: String?) {
        val uid=FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseFirestore.getInstance().collection("Users").
                document(uid).get().addOnSuccessListener {document->
            val userName=document.data?.get("userName").toString()
                    //update boolean
            //add doc in booking
        }.addOnFailureListener {
            Log.i("paysuccess",it.message.toString())
        }

    }

    override fun onPaymentError(p0: Int, p1: String?) {
        TODO("Not yet implemented")
    }
}