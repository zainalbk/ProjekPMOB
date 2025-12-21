package com.example.proyekinotekai.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyekinotekai.R
import com.example.proyekinotekai.ui.landing.LandingPage
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val tilEmail = findViewById<TextInputLayout>(R.id.tilRegEmail)
        val etEmail = findViewById<TextInputEditText>(R.id.etRegEmail)
        val tilPass = findViewById<TextInputLayout>(R.id.tilRegPass)
        val etPass = findViewById<TextInputEditText>(R.id.etRegPass)
        val tilConfirm = findViewById<TextInputLayout>(R.id.tilRegConfirm)
        val etConfirm = findViewById<TextInputEditText>(R.id.etRegConfirm)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)
        val btnBack = findViewById<ImageView>(R.id.btnBack2) // ID dari activity_register.xml

        btnNext.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString().trim()
            val confirm = etConfirm.text.toString().trim()

            // Reset Error
            tilEmail.error = null
            tilConfirm.error = null

            var isValid = true

            // Validasi Email
            if (!email.contains("@")) {
                tilEmail.error = "Email tidak Valid"
                etEmail.text?.clear() // Clear form sesuai request
                isValid = false
            }

            // Validasi Password Match
            if (pass != confirm) {
                tilConfirm.error = "PASSWORD SALAH"
                etConfirm.text?.clear() // Clear form confirm password
                isValid = false
            }

            // Validasi Kosong (Tambahan safety)
            if (pass.isEmpty()) {
                tilPass.error = "Password tidak boleh kosong"
                isValid = false
            }

            if (email.isEmpty()) {
                tilPass.error = "Email tidak boleh kosong"
                isValid = false
            }

            if (confirm.isEmpty()) {
                tilPass.error = "Password tidak boleh kosong"
                isValid = false
            }

            if (isValid) {
                val bioFragment = BioFragment()
                val bundle = Bundle()
                bundle.putString("EMAIL", email)
                bundle.putString("PASS", pass)
                bioFragment.arguments = bundle // Kirim data ke fragment

                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragmentContainer, bioFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // Kembali ke Landing Page
        btnBack.setOnClickListener {
            val intent = Intent(this, LandingPage::class.java)
            startActivity(intent)
            finish()
        }
    }
}