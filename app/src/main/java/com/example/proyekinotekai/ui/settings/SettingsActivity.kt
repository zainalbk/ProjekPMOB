package com.example.proyekinotekai.ui.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.proyekinotekai.MainActivity
import com.example.proyekinotekai.R
import com.example.proyekinotekai.data.UserRepository
import com.example.proyekinotekai.databinding.ActivitySettingsBinding
import com.example.proyekinotekai.databinding.DialogChangePasswordBinding
import com.example.proyekinotekai.ui.landing.LandingPage
import com.google.firebase.auth.EmailAuthProvider
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
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.icHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnEditProfile.setOnClickListener { 
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        binding.btnChangePassword.setOnClickListener { showChangePasswordDialog() }

        binding.btnSettings.setOnClickListener { 
            startActivity(Intent(this, SettingsDetailActivity::class.java))
        }

        binding.btnAboutUs.setOnClickListener { 
            startActivity(Intent(this, AboutActivity::class.java))
        }
        binding.btnSupport.setOnClickListener { 
            startActivity(Intent(this, SupportActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showChangePasswordDialog() {
        val dialogBinding = DialogChangePasswordBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.btnSubmitChangePassword.setOnClickListener {
            // Clear previous errors
            dialogBinding.tilOldPassword.error = null
            dialogBinding.tilNewPassword.error = null
            dialogBinding.tilConfirmPassword.error = null

            val oldPass = dialogBinding.etOldPassword.text.toString()
            val newPass = dialogBinding.etNewPassword.text.toString()
            val confirmPass = dialogBinding.etConfirmPassword.text.toString()

            if (newPass.length < 6) {
                dialogBinding.tilNewPassword.error = "Password minimal 6 karakter"
                return@setOnClickListener
            }
            if (newPass != confirmPass) {
                dialogBinding.tilConfirmPassword.error = "Password tidak cocok"
                return@setOnClickListener
            }

            performChangePassword(oldPass, newPass, dialog, dialogBinding)
        }

        dialog.show()
    }

    private fun performChangePassword(oldPass: String, newPass: String, dialog: AlertDialog, dialogBinding: DialogChangePasswordBinding) {
        val user = auth.currentUser
        if (user?.email == null) {
            Toast.makeText(this, "Gagal, sesi tidak valid.", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(user.email!!, oldPass)
        user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
            if (reAuthTask.isSuccessful) {
                user.updatePassword(newPass).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        dialog.dismiss()
                        Toast.makeText(this, "Password berhasil diperbarui, silakan login kembali.", Toast.LENGTH_LONG).show()
                        performLogout()
                    } else {
                        dialogBinding.tilNewPassword.error = updateTask.exception?.message ?: "Gagal memperbarui password"
                    }
                }
            } else {
                dialogBinding.tilOldPassword.error = "Password lama salah"
            }
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