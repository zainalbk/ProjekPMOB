package com.example.proyekinotekai.ui.iot // Sesuaikan dengan package Anda

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.proyekinotekai.R // Pastikan R diimpor dengan benar
import com.example.proyekinotekai.databinding.PhMonitoringBinding // Kelas binding dari ph_monitoring.xml
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhMonitoringActivity : AppCompatActivity() {

    // Deklarasi variabel binding untuk berkomunikasi dengan file XML
    private lateinit var binding: PhMonitoringBinding
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menghubungkan kelas Kotlin ini dengan file layout ph_monitoring.xml
        binding = PhMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance()

        // BAGIAN KOMUNIKASI 1: Memberi fungsi pada tombol kembali (btnBack)
        binding.btnBack.setOnClickListener {
            // Perintah ini akan menutup activity ini dan kembali ke MainActivity
            onBackPressedDispatcher.onBackPressed()
        }

        // BAGIAN KOMUNIKASI 2: Memulai pengambilan data dari Firebase untuk ditampilkan di UI
        fetchPhDataFromFirebase()
    }

    private fun fetchPhDataFromFirebase() {
        val phRef = database.getReference("dataiot/ph") // Sesuaikan path jika perlu

        phRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val phValue = snapshot.getValue(Float::class.java)
                if (phValue != null) {
                    // Data diterima, kirim ke fungsi untuk update UI
                    updateUiWithPhData(phValue)
                } else {
                    // Handle jika data tidak ada atau null
                    binding.txtPhValue.text = "N/A"
                    Toast.makeText(this@PhMonitoringActivity, "Gagal memuat data pH.", Toast.LENGTH_SHORT).show()
                }
                // Selalu update waktu terakhir pembaruan
                updateLastUpdatedTimestamp()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle jika koneksi ke Firebase gagal
                binding.txtPhValue.text = "-"
                Log.e("PhMonitoringActivity", "Firebase Error: ${error.message}")
                Toast.makeText(this@PhMonitoringActivity, "Gagal terhubung ke server.", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateUiWithPhData(ph: Float) {
        // Mengisi data ke dalam komponen UI di ph_monitoring.xml
        binding.txtPhValue.text = String.format(Locale.US, "%.1f", ph)

        // Logika untuk mengubah status, teks, dan warna berdasarkan nilai pH
        when {
            ph in 6.5..7.5 -> { // Kondisi Optimal
                binding.txtCondition.text = getString(R.string.ph_optimal)
                binding.txtRange.text = getString(R.string.ph_range_optimal)
                binding.chipStatus.text = getString(R.string.ph_normal)
                binding.chipStatus.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.teal_700))
            }
            ph < 6.5 -> { // Kondisi Asam
                binding.txtCondition.text = getString(R.string.ph_asam)
                binding.txtRange.text = getString(R.string.ph_range_asam, 6.5)
                binding.chipStatus.text = getString(R.string.ph_asam)
                binding.chipStatus.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange_warning)) // Pastikan warna ini ada
            }
            else -> { // Kondisi Basa (ph > 7.5)
                binding.txtCondition.text = getString(R.string.ph_basa)
                binding.txtRange.text = getString(R.string.ph_range_basa, 7.5)
                binding.chipStatus.text = getString(R.string.ph_basa)
                binding.chipStatus.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue_alkaline)) // Pastikan warna ini ada
            }
        }
    }

    private fun updateLastUpdatedTimestamp() {
        val sdf = SimpleDateFormat("'diperbarui pada' HH:mm", Locale.getDefault())
        binding.txtLastUpdateStatus.text = sdf.format(Date())
    }
}
