package com.example.proyekinotekai.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyekinotekai.data.UserRepository
import com.example.proyekinotekai.data.local.UserEntity
import com.example.proyekinotekai.databinding.FragmentBioBinding
import com.example.proyekinotekai.ui.landing.LandingPage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.launch

class BioFragment : Fragment() {

    private var _binding: FragmentBioBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var repository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        repository = UserRepository(requireContext())

        val email = arguments?.getString("EMAIL") ?: ""
        val password = arguments?.getString("PASS") ?: ""

        binding.btnRegisterFinal.setOnClickListener {
            val nama = binding.etNama.text.toString().trim()
            val telp = binding.etTelp.text.toString().trim()
            val alamat = binding.etAlamat.text.toString().trim()

            if (nama.isEmpty() || telp.isEmpty() || alamat.isEmpty()) {
                Toast.makeText(context, "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(email, password, nama, telp, alamat)
        }

        binding.btnBack3.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun registerUser(email: String, pass: String, nama: String, telp: String, alamat: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val authUid = auth.currentUser?.uid
                    if (authUid == null) {
                        Toast.makeText(context, "Gagal mendapatkan Auth ID", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }

                    val counterRef = database.child("counters").child("peternak_count")
                    counterRef.runTransaction(object : Transaction.Handler {
                        override fun doTransaction(currentData: MutableData): Transaction.Result {
                            val currentCount = currentData.getValue(Int::class.java) ?: 0
                            currentData.value = currentCount + 1
                            return Transaction.success(currentData)
                        }

                        override fun onComplete(error: DatabaseError?, committed: Boolean, finalData: DataSnapshot?) {
                            if (committed && finalData != null) {
                                val newIdNumber = finalData.getValue(Int::class.java) ?: 1
                                val customUserId = String.format("Peternak%04d", newIdNumber)

                                // Simpan pemetaan Auth UID -> Custom ID
                                database.child("user_id_map").child(authUid).setValue(customUserId)

                                repository.registerUserToFirebaseAndLocal(customUserId, email, pass, nama, telp, alamat) { success, msg ->
                                    if (success) {
                                        lifecycleScope.launch {
                                            val userEntity = UserEntity(customUserId, email, nama, telp, alamat)
                                            repository.saveToLocal(userEntity)

                                            Toast.makeText(context, "Registrasi Berhasil! ID Anda: $customUserId", Toast.LENGTH_LONG).show()
                                            val intent = Intent(activity, LandingPage::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                            activity?.finish()
                                        }
                                    } else {
                                        Toast.makeText(context, "Gagal menyimpan data: $msg", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Gagal mendapatkan ID: ${error?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                } else {
                    Toast.makeText(context, "Gagal membuat akun: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
