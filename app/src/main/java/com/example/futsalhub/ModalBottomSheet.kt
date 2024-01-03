package com.example.futsalhub

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import com.example.futsalhub.databinding.BottomSheetBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ModalBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetBinding
    private lateinit var selectedSort: String
    private lateinit var priceFilter: String
    private lateinit var listener: SortAndFilterListener
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val checkedLocations: MutableList<String> = mutableListOf()
    private val checkedTypes: MutableList<String> = mutableListOf()

    private val dataStore: DataStore<Preferences> by lazy {
        requireContext().createDataStore(name = "settings")
    }
    fun setSortAndFilterListener(listener: SortAndFilterListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = BottomSheetBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            try {
                val values = read(listOf("sortval", "checkedLocations", "checkedTypes"))
                selectedSort= values["sortval"].toString()

                Log.i("tag123",selectedSort)

                when (selectedSort) {
                    "ovrRating" -> {
                        binding.tbSort.check(R.id.sort_rating)
                    }
                    "distance" -> {
                        binding.tbSort.check(R.id.sort_distance)
                    }
                    else -> {
                        binding.tbSort.check(R.id.sort_name)
                    }
                }

                val locations: List<String> = values["checkedLocations"]!!.removeSurrounding("[", "]").split(", ")
                val totalLocations: List<String> = listOf("North Goa","South Goa")
                val subLocations= totalLocations.subtract(locations)

                for(l in subLocations){
                    if(l=="North Goa"){
                        binding.chpNorthGoa.isChecked=false
                    }
                    else if (l=="South Goa"){
                        binding.chpSouthGoa.isChecked=false
                    }
                }

                val types: List<String> = values["checkedTypes"]!!.removeSurrounding("[", "]").split(", ")
                val totalTypes: List<String> = listOf("5-a side","6-a side","7-a side")
                val subTypes= totalTypes.subtract(types.toSet())

                for(t in subTypes){
                    when (t){
                        "5-a side"->{
                            binding.chpFive.isChecked=false
                        }
                        "6-a side"->{
                            binding.chpSix.isChecked=false
                        }
                        "7-a side"->{
                            binding.chpSeven.isChecked=false
                        }
                    }
                }
            } catch (e: Exception) {
                Log.i("tag123", "Error reading DataStore: ${e.message}")
            }
        }

        defaultFilters()

        checkSort()

        binding.sbPrice.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.tvLast.text="₹"+progress.toString()
                priceFilter=progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })

        binding.btnApply.setOnClickListener {
            checkFilter()

            listener.sortAndFilterData(
                selectedSort,
                latitude,
                longitude,
                priceFilter,
                checkedLocations,
                checkedTypes
            )

            lifecycleScope.launch {
                val keyValues = mapOf(
                    "sortval" to selectedSort,
                    "checkedLocations" to checkedLocations,
                    "checkedTypes" to checkedTypes
                )
                save(keyValues)
            }
            dismiss()
        }
        return binding.root
    }

    private fun defaultFilters() {
        if(!binding.chpNorthGoa.isChecked && !binding.chpSouthGoa.isChecked){
            binding.chpGrpLocation.check(R.id.chpNorthGoa)
            binding.chpGrpLocation.check(R.id.chpSouthGoa)

        }

        if(!binding.chpFive.isChecked && !binding.chpSix.isChecked && !binding.chpSeven.isChecked){
            binding.chpGrpType.check(R.id.chpFive)
            binding.chpGrpType.check(R.id.chpSix)
            binding.chpGrpType.check(R.id.chpSeven)
        }
    }
    
    private suspend fun save(keyValuePairs: Map<String, Any>){
        dataStore.edit { settings->
            keyValuePairs.forEach { (key, value) ->
                settings[preferencesKey<String>(key)] = value.toString()
            }
        }
    }

    private suspend fun read(keys: List<String>): Map<String, String?> {
        val dataStoreKeys = keys.map { preferencesKey<String>(it) }
        val preferences = dataStore.data.first()
        val result = mutableMapOf<String, String?>()
        dataStoreKeys.forEach { key ->
            result[key.name] = preferences[key]
        }
        return result
    }

    private fun checkSort() {
        binding.tbSort.addOnButtonCheckedListener { _, checkedId, isChecked ->
            // not observing the unchecked value
            if (!isChecked) return@addOnButtonCheckedListener

            when (checkedId) {
                R.id.sort_name -> {
                    selectedSort = "groundName"
                }

                R.id.sort_rating -> {
                    selectedSort = "ovrRating"
                }

                R.id.sort_distance -> {
                    fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(requireActivity())
                    fetchLocation()
                    selectedSort = "distance"
                }
            }
        }
    }

    private fun checkFilter() {
        if (binding.chpNorthGoa.isChecked) {
            checkedLocations.add("North Goa")
        }
        if (binding.chpSouthGoa.isChecked) {
            checkedLocations.add("South Goa")
        }

        if (binding.chpFive.isChecked) {
            checkedTypes.add("5-a side")
        }
        if (binding.chpSix.isChecked) {
            checkedTypes.add("6-a side")
        }
        if (binding.chpSeven.isChecked) {
            checkedTypes.add("7-a side")
        }
    }

    private fun fetchLocation() {
        val task = fusedLocationClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // request permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            Log.i("location123", "done")
            return
        }
        task.addOnSuccessListener {
            if (it != null) {
                latitude = it.latitude
                longitude = it.longitude
                Log.i("location123", it.latitude.toString() + it.longitude.toString())
            }
        }
    }


    interface SortAndFilterListener {
        fun sortAndFilterData(
            sortType: String,
            latitude: Double,
            longitude: Double,
            priceFilter: String,
            filterLocations: MutableList<String>,
            filterType: MutableList<String>
        )
    }
}