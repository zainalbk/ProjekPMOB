package com.example.proyekinotekai.ui.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.proyekinotekai.MainActivity
import com.example.proyekinotekai.R
import com.example.proyekinotekai.data.UserRepository
import com.example.proyekinotekai.databinding.ActivitySettingsBinding
import com.example.proyekinotekai.ui.landing.LandingPage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        repository = UserRepository(this)

        loadUserData()
        setupClickListeners()
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.tvUserEmail.text = currentUser.email ?: "No Email"

            repository.getCustomUserId(currentUser.uid) { customId ->
                if (customId != null) {
                    lifecycleScope.launch {
                        val user = repository.getUserById(customId)
                        if (user != null) {
                            binding.tvUserName.text = user.nama

                            // Load gambar profil dan crop menjadi lingkaran
                            user.profilePictureUrl?.let {
                                if (it.isNotEmpty()) {
                                    Glide.with(this@SettingsActivity).load(it).circleCrop().into(binding.ivProfileAvatar)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.icHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnEditProfile.setOnClickListener { 
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        binding.btnChangePassword.setOnClickListener { showToast("Masuk ke Ganti Password") }

        binding.btnSettings.setOnClickListener { showToast("Masuk ke Halaman Pengaturan") }

        binding.btnAboutUs.setOnClickListener { showToast("Tentang Kami") }
        binding.btnSupport.setOnClickListener { showToast("Dukungan & Bantuan") }

        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmLogout)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelLogout)

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            performLogout()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun performLogout() {
        auth.signOut()
        lifecycleScope.launch {
            repository.clearLocalUser()
            val intent = Intent(this@SettingsActivity, LandingPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}