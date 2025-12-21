package com.example.proyekinotekai.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyekinotekai.MainActivity
import com.example.proyekinotekai.databinding.LandingPageBinding
import com.example.proyekinotekai.ui.login.LoginActivity
import com.example.proyekinotekai.ui.login.RegisterActivity

class LandingPage : AppCompatActivity() {

    private lateinit var binding: LandingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnGetStarted.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
