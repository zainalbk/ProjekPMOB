package com.example.proyekinotekai

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.proyekinotekai.data.UserRepository
import com.example.proyekinotekai.ui.landing.LandingPage
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var repository: UserRepository

    private lateinit var tvUsername: TextView
    private lateinit var btnHome: FrameLayout
    private lateinit var cvProfile: CardView

    // Dashboard Cards
    private lateinit var cardPakan: MaterialCardView
    private lateinit var cardPh: MaterialCardView
    private lateinit var cardPerangkat: MaterialCardView

    // Feature & History Cards (BARU)
    private lateinit var cardFeatureAi: MaterialCardView
    private lateinit var cardHistoryPh: MaterialCardView
    private lateinit var cardHistoryPakan: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        repository = UserRepository(this)

        initViews()
        checkUserSession()
        setupBottomNavigation()
        setupDashboardClicks()

        val dateTextView: TextView = findViewById(R.id.tvCurrentDate)

        // Get the current date
        val currentDate = Date()

        // Format the date to "dd MMMM" format (e.g., "17 June"). [3, 12]
        val dateFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        // Set the formatted date to the TextView
        dateTextView.text = " $formattedDate"
    }

    private fun initViews() {
        tvUsername = findViewById(R.id.tvUsername)
        btnHome = findViewById(R.id.homeContainer)
        cvProfile = findViewById(R.id.cvProfile)

        // Grid Cards
        cardPakan = findViewById(R.id.cardPakan)
        cardPh = findViewById(R.id.cardPh)
        cardPerangkat = findViewById(R.id.cardPerangkat)

        // New Cards (Inisialisasi ID baru)
        cardFeatureAi = findViewById(R.id.cardFeatureAi)
        cardHistoryPh = findViewById(R.id.cardHistoryPh)
        cardHistoryPakan = findViewById(R.id.cardHistoryPakan)
    }

    private fun checkUserSession() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            redirectToLandingPage()
            return
        }

        repository.getCustomUserId(currentUser.uid) { customId ->
            if (customId != null) {
                loadUserData(customId)
                repository.listenForUserChanges(customId)
            } else {
                Toast.makeText(this, "Gagal sinkronisasi data pengguna.", Toast.LENGTH_SHORT).show()
                auth.signOut()
                redirectToLandingPage()
            }
        }
    }

    private fun loadUserData(userId: String) {
        lifecycleScope.launch {
            val user = repository.getUserById(userId)
            if (user != null) {
                tvUsername.text = "${user.nama} Yay!"
            } else {
                tvUsername.text = "Guest!"
            }
        }
    }

    private fun setupBottomNavigation() {
        btnHome.setOnClickListener {
            Toast.makeText(this, "Refreshing Dashboard...", Toast.LENGTH_SHORT).show()
        }

        cvProfile.setOnClickListener {
            auth.signOut()
            lifecycleScope.launch {
                repository.clearLocalUser()
                redirectToLandingPage()
            }
        }
    }

    private fun redirectToLandingPage() {
        val intent = Intent(this@MainActivity, LandingPage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupDashboardClicks() {
        // Klik Grid Utama
        cardPakan.setOnClickListener {
            Toast.makeText(this, "Membuka Detail Pakan...", Toast.LENGTH_SHORT).show()
        }
        cardPh.setOnClickListener {
            Toast.makeText(this, "Membuka Detail pH...", Toast.LENGTH_SHORT).show()
        }
        cardPerangkat.setOnClickListener {
            Toast.makeText(this, "Mengelola Perangkat...", Toast.LENGTH_SHORT).show()
        }

        // Klik Fitur & History Baru
        cardFeatureAi.setOnClickListener {
            Toast.makeText(this, "Membuka AI Optimization...", Toast.LENGTH_SHORT).show()
        }
        cardHistoryPh.setOnClickListener {
            Toast.makeText(this, "History pH diklik", Toast.LENGTH_SHORT).show()
        }
        cardHistoryPakan.setOnClickListener {
            Toast.makeText(this, "History Pakan diklik", Toast.LENGTH_SHORT).show()
        }
    }
}