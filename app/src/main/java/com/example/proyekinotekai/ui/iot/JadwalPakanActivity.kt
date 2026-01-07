package com.example.proyekinotekai

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class JadwalPakanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Atur layout XML untuk activity ini
        setContentView(R.layout.jadwal_pakan)

        // Inisialisasi tombol kembali dari layout jadwal_pakan.xml
        val btnBack: ImageButton = findViewById(R.id.btnBack)

        // Atur listener klik untuk tombol kembali
        btnBack.setOnClickListener {
            // Menutup activity saat ini dan kembali ke activity sebelumnya (MainActivity)
            finish()
        }
    }
}
