package com.example.proyekinotekai.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyekinotekai.MainActivity
import com.example.proyekinotekai.R
import com.example.proyekinotekai.ui.landing.LandingPage
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // UI Variables
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnBack: ImageView
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Inisialisasi View
        initViews()

        // Tombol Login
        btnLogin.setOnClickListener {
            handleLogin()
        }

        // Tombol Back
        btnBack.setOnClickListener {
            val intent = Intent(this, LandingPage::class.java)
            startActivity(intent)
            finish()
        }

        // Link Register
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Social Login (Hanya tampilan/logika dummy)
        setupSocialLogin()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        btnLogin = findViewById(R.id.btnActionLogin)
        btnBack = findViewById(R.id.btnBack)
        tvRegister = findViewById(R.id.tvRegisterLink)
    }

    private fun handleLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Reset Error State
        tilEmail.error = null
        tilPassword.error = null
        tilEmail.isErrorEnabled = false
        tilPassword.isErrorEnabled = false

        // Validasi Input
        if (isValidInput(email, password)) {
            showLoading(true)

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    showLoading(false)
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // Tampilkan Error Merah sesuai desain
                        tilPassword.error = "Email atau sandi salah, harap cek kembali"
                        Toast.makeText(this, "Login Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun isValidInput(email: String, pass: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            tilEmail.error = "Harap masukkan email dengan benar"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Format email tidak valid"
            isValid = false
        }

        if (pass.isEmpty()) {
            tilPassword.error = "Harap masukkan sandi dengan benar"
            isValid = false
        }

        return isValid
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        btnLogin.isEnabled = !isLoading
        btnLogin.text = if (isLoading) "Loading..." else "Login"
    }

    private fun setupSocialLogin() {
        findViewById<ImageView>(R.id.btnGoogle).setOnClickListener {
            Toast.makeText(this, "Login Google diklik", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.btnFacebook).setOnClickListener {
            Toast.makeText(this, "Login Facebook diklik", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.btnApple).setOnClickListener {
            Toast.makeText(this, "Login Apple diklik", Toast.LENGTH_SHORT).show()
        }
    }
}
