package com.example.proyekinotekai

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyekinotekai.databinding.ActivityMainBinding
import com.google.android.material.progressindicator.CircularProgressIndicator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cpiFeed.isIndeterminate = false
        binding.cpiFeed.setProgressCompat(98, true)

        // Safe area
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom)
            insets
        }

        // Demo data
        binding.txtWelcomeName.text = "Mustofa Abdurrahim"
        binding.txtEnergyToday.text = "5.0 w/h"
        binding.txtEnergyMonth.text = "0.005 kWh"

        binding.txtLastPhValue.text = "7.3"
        binding.txtLastFeedStatus.text = "Succes"
        binding.txtNextFeed.text = "Next: 15:00"

        // Clicks (dummy)
        binding.fabHome.setOnClickListener { /* TODO */ }
        binding.btnDevice.setOnClickListener { /* TODO */ }
        binding.btnAccount.setOnClickListener { /* TODO */ }
    }
}
