package com.example.proyekinotekai.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyekinotekai.data.UserRepository
import com.example.proyekinotekai.databinding.ActivitySupportBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupportBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        repository = UserRepository(this)

        setupHeader()
        setupClickListeners()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Ambil nama dari database, bukan dari Auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            repository.getCustomUserId(currentUser.uid) { customId ->
                if (customId != null) {
                    lifecycleScope.launch {
                        val user = repository.getUserById(customId)
                        val name = user?.nama ?: "Pengguna"
                        binding.tvGreeting.text = "Hi, $name! Jangan ragu untuk menghubungi kami melalui salah satu saluran di bawah ini."
                    }
                } else {
                    binding.tvGreeting.text = "Hi, Pengguna! Jangan ragu untuk menghubungi kami melalui salah satu saluran di bawah ini."
                }
            }
        } else {
            binding.tvGreeting.text = "Hi, Pengguna! Jangan ragu untuk menghubungi kami melalui salah satu saluran di bawah ini."
        }
    }

    private fun setupClickListeners() {
        binding.cardCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:081248800707")
            startActivity(intent)
        }

        binding.cardEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("2300018015@webmail.uad.ac.id"))
                putExtra(Intent.EXTRA_SUBJECT, "Bantuan Aplikasi SiPakan")
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        binding.cardChat.setOnClickListener {
            val phoneNumber = "087722988738"
            val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        binding.cardInstagram.setOnClickListener {
            val url = "https://www.instagram.com/fuadmbrr/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }
}