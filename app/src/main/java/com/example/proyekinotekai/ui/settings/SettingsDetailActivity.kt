package com.example.proyekinotekai.ui.settings

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Filter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyekinotekai.R
import com.example.proyekinotekai.data.UserRepository
import com.example.proyekinotekai.databinding.ActivitySettingsDetailBinding
import com.example.proyekinotekai.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class SettingsDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var repository: UserRepository
    private var currentCustomId: String? = null
    private val languages = listOf("Pilih Bahasa", "Bahasa Indonesia", "English")
    private lateinit var formattedTimezones: List<String>

    private val activeColor = ColorStateList.valueOf(Color.parseColor("#4DB6AC")) // Teal
    private val grayColor = ColorStateList.valueOf(Color.GRAY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        repository = UserRepository(this)

        setupNotificationSwitch()
        setupLanguageSpinner()
        setupTimezoneSpinner()
        setupAccountActions()
        loadUserSettings()

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupNotificationSwitch() {
        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                Toast.makeText(this, "Notification is shut down", Toast.LENGTH_SHORT).show()
                binding.switchNotification.thumbTintList = grayColor
                binding.switchNotification.trackTintList = grayColor
            } else {
                binding.switchNotification.thumbTintList = activeColor
                binding.switchNotification.trackTintList = activeColor
            }
        }
    }

    private fun setupLanguageSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)
        binding.spinnerLanguage.adapter = adapter

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = languages[position]
                if (position > 0 && currentCustomId != null) { // Jangan simpan jika memilih "Pilih Bahasa"
                    saveLanguagePreference(selectedLanguage)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupTimezoneSpinner() {
        formattedTimezones = TimeZone.getAvailableIDs().map { id ->
            val tz = TimeZone.getTimeZone(id)
            val offset = tz.rawOffset
            val hours = TimeUnit.MILLISECONDS.toHours(offset.toLong())
            val minutes = TimeUnit.MILLISECONDS.toMinutes(offset.toLong()) % 60
            "(GMT%+d:%02d) %s".format(hours, minutes, id.replace("_", " "))
        }.sorted()

        // Pass a mutable copy to the adapter
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, formattedTimezones.toMutableList()) {
            override fun getFilter(): Filter {
                return object : Filter() {
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        val queryString = constraint?.toString()?.lowercase(Locale.ROOT)
                        val filterResults = FilterResults()
                        filterResults.values = if (queryString.isNullOrBlank()) {
                            formattedTimezones
                        } else {
                            formattedTimezones.filter {
                                it.lowercase(Locale.ROOT).contains(queryString)
                            }
                        }
                        return filterResults
                    }

                    @Suppress("UNCHECKED_CAST")
                    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                        clear()
                        addAll(results.values as List<String>)
                        notifyDataSetChanged()
                    }
                }
            }
        }

        binding.autoCompleteTimezone.setAdapter(adapter)

        binding.autoCompleteTimezone.setOnItemClickListener { parent, _, position, _ ->
            val selectedTimezone = parent.getItemAtPosition(position) as? String
            if (currentCustomId != null && selectedTimezone != null) {
                saveTimezonePreference(selectedTimezone)
            }
        }
    }

    private fun loadUserSettings() {
        val currentUser = auth.currentUser ?: return
        repository.getCustomUserId(currentUser.uid) { customId ->
            currentCustomId = customId
            if (customId != null) {
                lifecycleScope.launch {
                    val user = repository.getUserById(customId)
                    user?.language?.let {
                        val langIndex = languages.indexOf(it)
                        if (langIndex != -1) {
                            binding.spinnerLanguage.setSelection(langIndex)
                        }
                    }
                    user?.timezone?.let {
                        binding.autoCompleteTimezone.setText(it, false)
                    }
                }
            }
        }
    }

    private fun saveLanguagePreference(language: String) {
        val updateData = mapOf("language" to language)
        repository.updateUser(currentCustomId!!, updateData) { success ->
            if (success) {
                Toast.makeText(this, "Bahasa disimpan: $language", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menyimpan preferensi bahasa", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveTimezonePreference(timezone: String) {
        val updateData = mapOf("timezone" to timezone)
        repository.updateUser(currentCustomId!!, updateData) { success ->
            if (success) {
                Toast.makeText(this, "Zona waktu disimpan: $timezone", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menyimpan preferensi zona waktu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAccountActions() {
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.btnDeleteAccount.setOnClickListener {
            showDeleteConfirmation()
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
            val intent = Intent(this@SettingsDetailActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showDeleteConfirmation() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_account, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmDelete)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelDelete)

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            performDeleteAccount()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun performDeleteAccount() {
        val user = auth.currentUser
        if (user == null || currentCustomId == null) {
            Toast.makeText(this, "Gagal, sesi tidak ditemukan.", Toast.LENGTH_SHORT).show()
            return
        }

        repository.deleteUserFromDatabase(user.uid, currentCustomId!!) { success, message ->
            if (success) {
                user.delete().addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        lifecycleScope.launch {
                            repository.clearLocalUser()
                            Toast.makeText(this@SettingsDetailActivity, "Akun berhasil dihapus", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SettingsDetailActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(this, "Gagal menghapus akun: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Gagal menghapus data: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}