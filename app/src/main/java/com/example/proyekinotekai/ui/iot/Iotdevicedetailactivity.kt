package com.example.proyekinotekai.ui.iot

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyekinotekai.R

/**
 * IoT Device Detail Activity - Home/Dashboard
 * Displays comprehensive device information including:
 * - Feeding schedule and history
 * - pH monitoring with charts
 * - AI assistant recommendations
 * - Device statistics and controls
 */
class IoTDeviceDetailActivity : AppCompatActivity() {

    private lateinit var tvDeviceName: TextView
    private lateinit var tvDeviceId: TextView
    private lateinit var btnBack: ImageView

    private var deviceName: String = "Feeder 1"
    private var deviceId: String = "#D544388"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Replace with actual layout from Figma
        // For now, use simple layout for testing
        setContentView(R.layout.activity_iot_device_detail)

        // Get data from intent
        deviceName = intent.getStringExtra("DEVICE_NAME") ?: "Feeder 1"
        deviceId = intent.getStringExtra("DEVICE_ID") ?: "#D544388"

        initViews()
        setupClickListeners()
        loadDeviceData()
    }

    private fun initViews() {
        // Initialize views
        // TODO: Map all views from your Figma design

        // Example (adjust based on your actual layout):
        // tvDeviceName = findViewById(R.id.tvDeviceName)
        // tvDeviceId = findViewById(R.id.tvDeviceId)
        // btnBack = findViewById(R.id.btnBack)
    }

    private fun setupClickListeners() {
        // Back button
        // btnBack.setOnClickListener {
        //     finish()
        // }
    }

    private fun loadDeviceData() {
        // Set device info
        // tvDeviceName?.text = deviceName
        // tvDeviceId?.text = deviceId

        // TODO: Load real-time data from Firebase/API
        // - Feeding schedule
        // - pH readings
        // - Device status
        // - Historical data
    }

    companion object {
        const val TAG = "IoTDeviceDetailActivity"
    }
}