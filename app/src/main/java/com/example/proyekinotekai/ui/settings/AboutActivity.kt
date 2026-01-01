package com.example.proyekinotekai.ui.settings

import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyekinotekai.R
import com.example.proyekinotekai.databinding.ActivityAboutBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class AboutActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityAboutBinding
    private val targetLocation = LatLng(-7.801380, 110.364749)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupTeamData()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupTeamData() {
        binding.member1.tvName.text = "Muhammad Fuad B M"
        binding.member1.tvNim.text = "2300018015"

        binding.member2.tvName.text = "Muhammad Zidane H H"
        binding.member2.tvNim.text = "2300018003"

        binding.member3.tvName.text = "Zainal Basri K"
        binding.member3.tvNim.text = "2300018098"
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(MarkerOptions().position(targetLocation).title("Lokasi SiPakan"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 15f))
        googleMap.uiSettings.isScrollGesturesEnabled = false

        fetchAddressName(targetLocation)
    }

    private fun fetchAddressName(latLng: LatLng) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val addressText = address.getAddressLine(0)
                binding.tvAddress.text = addressText
            } else {
                binding.tvAddress.text = "Alamat tidak ditemukan"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvAddress.text = "Universitas Ahmad Dahlan IV"
        }
    }
}