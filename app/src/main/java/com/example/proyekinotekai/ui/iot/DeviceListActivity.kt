package com.example.proyekinotekai.ui.iot

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.proyekinotekai.R
import com.example.proyekinotekai.data.UserRepository
import com.example.proyekinotekai.ui.settings.EditProfileActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class DeviceListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var repository: UserRepository

    // Views
    private lateinit var ivProfile: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var btnNotification: ImageView
    private lateinit var btnAddDevice: ImageView
    private lateinit var btnEdit: ImageView
    private lateinit var btnDelete: ImageView
    private lateinit var cardFeeder1: CardView
    private lateinit var homeContainer: LinearLayout
    private lateinit var btnProfile: LinearLayout

    // Edit & Delete Mode
    private var isEditMode = false
    private var deviceName = "Feeder 1"
    private val deviceId = "#D544388"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        auth = FirebaseAuth.getInstance()
        repository = UserRepository(this)

        initViews()
        loadUserProfile()
        setupClickListeners()
    }

    private fun initViews() {
        ivProfile = findViewById(R.id.ivProfile)
        tvUsername = findViewById(R.id.tvUsername)
        btnNotification = findViewById(R.id.btnNotification)
        btnAddDevice = findViewById(R.id.btnAddDevice)
        btnEdit = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDelete)
        cardFeeder1 = findViewById(R.id.cardFeeder1)
        homeContainer = findViewById(R.id.homeContainer)
        btnProfile = findViewById(R.id.btnProfile)
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            tvUsername.text = "Guest"
            return
        }

        repository.getCustomUserId(currentUser.uid) { customId ->
            if (customId != null) {
                lifecycleScope.launch {
                    val user = repository.getUserById(customId)
                    user?.let {
                        tvUsername.text = it.nama

                        // Load profile picture
                        it.profilePictureUrl?.let { url ->
                            if (url.isNotEmpty()) {
                                Glide.with(this@DeviceListActivity)
                                    .load(url)
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_profile_placeholder)
                                    .into(ivProfile)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Header buttons
        btnNotification.setOnClickListener {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
        }

        btnAddDevice.setOnClickListener {
            Toast.makeText(this, "Add New Device", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to Add Device screen
        }

        // Edit Button - Show dialog to edit device name
        btnEdit.setOnClickListener {
            showEditDeviceNameDialog()
        }

        // Delete Button - Show confirmation dialog
        btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Card Feeder 1 - Navigate to Detail
        cardFeeder1.setOnClickListener {
            navigateToDeviceDetail()
        }

        // Bottom Navigation
        homeContainer.setOnClickListener {
            finish() // Back to MainActivity
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }

    /**
     * Navigate to IoT Device Detail Page (Home/Dashboard)
     */
    private fun navigateToDeviceDetail() {
        val intent = Intent(this, IoTDeviceDetailActivity::class.java)
        intent.putExtra("DEVICE_NAME", deviceName)
        intent.putExtra("DEVICE_ID", deviceId)
        startActivity(intent)
    }

    /**
     * Show Edit Device Name Dialog
     */
    private fun showEditDeviceNameDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialoge_edit_device)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Get views from dialog
        val etDeviceName = dialog.findViewById<TextInputEditText>(R.id.etDeviceName)
        val btnSubmit = dialog.findViewById<MaterialButton>(R.id.btnSubmit)

        // Set current device name
        etDeviceName.setText(deviceName)
        etDeviceName.setSelection(deviceName.length) // Move cursor to end

        // Submit button click
        btnSubmit.setOnClickListener {
            val newName = etDeviceName.text.toString().trim()

            if (newName.isEmpty()) {
                etDeviceName.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }

            // Update device name
            deviceName = newName
            updateDeviceNameUI()

            Toast.makeText(this, "Nama device berhasil diubah: $newName", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Show Delete Confirmation Dialog
     */
    private fun showDeleteConfirmationDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialoge_delete_device)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Get buttons from dialog
        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel) // "Iya"
        val btnConfirm = dialog.findViewById<MaterialButton>(R.id.btnConfirm) // "Batalkan"

        // "Iya" button - Delete device
        btnCancel.setOnClickListener {
            // Perform delete action
            deleteDevice()
            dialog.dismiss()
        }

        // "Batalkan" button - Cancel deletion
        btnConfirm.setOnClickListener {
            Toast.makeText(this, "Pembatalan penghapusan", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Delete Device
     */
    private fun deleteDevice() {
        // TODO: Implement actual deletion from Firebase/Database

        Toast.makeText(this, "Device $deviceName telah dihapus", Toast.LENGTH_SHORT).show()

        // Navigate back to MainActivity after deletion
        finish()
    }

    /**
     * Update Device Name in UI
     */
    private fun updateDeviceNameUI() {
        // Find the TextView in cardFeeder1 and update it
        val tvDeviceName = cardFeeder1.findViewById<TextView>(R.id.tvDeviceName)
        tvDeviceName?.text = deviceName
    }

    companion object {
        const val TAG = "DeviceListActivity"
    }
}