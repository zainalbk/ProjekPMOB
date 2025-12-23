package com.example.proyekinotekai.ui.settings

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.proyekinotekai.data.UserRepository
import com.example.proyekinotekai.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Calendar

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var repository: UserRepository
    private var currentCustomId: String? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        repository = UserRepository(this)

        setupListeners()
        loadUserData()
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser ?: return
        binding.tvCurrentEmail.text = currentUser.email

        repository.getCustomUserId(currentUser.uid) { customId ->
            currentCustomId = customId
            if (customId != null) {
                lifecycleScope.launch {
                    val user = repository.getUserById(customId)
                    if (user != null) {
                        binding.tvCurrentName.text = user.nama
                        binding.etName.setText(user.nama)
                        binding.etEmail.setText(user.email)
                        binding.etPhone.setText(user.noTelp)
                        binding.etAddressShort.setText(user.alamat)
                        user.tanggalLahir?.let { splitDateToBoxes(it) }

                        // Load gambar profil dengan Glide
                        user.profilePictureUrl?.let {
                            Glide.with(this@EditProfileActivity).load(it).into(binding.ivProfile)
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        val dateClickAction = { showDatePicker() }
        binding.etDay.setOnClickListener { dateClickAction() }
        binding.etMonth.setOnClickListener { dateClickAction() }
        binding.etYear.setOnClickListener { dateClickAction() }

        binding.btnSubmit.setOnClickListener { saveProfileChanges() }

        binding.btnChangePhoto.setOnClickListener { openGallery() }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            imageUri = it.data?.data
            binding.ivProfile.setImageURI(imageUri)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                binding.etDay.text = String.format("%02d", day)
                binding.etMonth.text = String.format("%02d", month + 1)
                binding.etYear.text = year.toString()
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveProfileChanges() {
        if (currentCustomId == null) {
            showToast("Gagal menyimpan, ID pengguna tidak ditemukan.")
            return
        }

        if (imageUri != null) {
            uploadImageToCloudinaryAndSaveData()
        } else {
            saveDataToFirebase(null)
        }
    }

    private fun uploadImageToCloudinaryAndSaveData() {
        MediaManager.get().upload(imageUri).callback(object : UploadCallback {
            override fun onStart(requestId: String) { showToast("Mengunggah gambar...") }
            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                val imageUrl = resultData["url"].toString()
                saveDataToFirebase(imageUrl)
            }
            override fun onError(requestId: String, error: ErrorInfo) { showToast("Gagal mengunggah gambar: ${error.description}") }
            override fun onReschedule(requestId: String, error: ErrorInfo) {}
        }).dispatch()
    }

    private fun saveDataToFirebase(imageUrl: String?) {
        val updateData = mutableMapOf<String, Any?>(
            "nama" to binding.etName.text.toString(),
            "noTelp" to binding.etPhone.text.toString(),
            "alamat" to binding.etFullAddress.text.toString(),
            "tanggalLahir" to "${binding.etDay.text}-${binding.etMonth.text}-${binding.etYear.text}"
        )

        if (imageUrl != null) {
            updateData["profilePictureUrl"] = imageUrl
        }

        repository.updateUser(currentCustomId!!, updateData) { success ->
            if (success) {
                showToast("Profil berhasil diperbarui")
                finish()
            } else {
                showToast("Gagal memperbarui profil")
            }
        }
    }

    private fun splitDateToBoxes(date: String) {
        val parts = date.split("-")
        if (parts.size == 3) {
            binding.etDay.text = parts[0]
            binding.etMonth.text = parts[1]
            binding.etYear.text = parts[2]
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}