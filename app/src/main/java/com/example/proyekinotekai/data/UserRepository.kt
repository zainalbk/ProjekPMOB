package com.example.proyekinotekai.data

import android.content.Context
import com.example.proyekinotekai.data.local.AppDatabase
import com.example.proyekinotekai.data.local.UserDao
import com.example.proyekinotekai.data.local.UserEntity
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserRepository(context: Context) {

    private val userDao: UserDao = AppDatabase.getDatabase(context).userDao()
    private val usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private val userIdMapRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("user_id_map")

    fun registerUserToFirebaseAndLocal(
        id: String, email: String, pass: String,
        nama: String, telp: String, alamat: String,
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        val userData = hashMapOf<String, Any>(
            "id" to id,
            "email" to email,
            "password" to pass, // PERINGATAN: Menyimpan password dalam bentuk plain text tidak aman.
            "nama" to nama,
            "noTelp" to telp,
            "alamat" to alamat
        )

        usersRef.child(id).setValue(userData)
            .addOnSuccessListener { onResult(true, "Sukses registrasi ke Firebase.") }
            .addOnFailureListener { e -> onResult(false, e.message ?: "Terjadi kesalahan.") }
    }

    suspend fun saveToLocal(userEntity: UserEntity) {
        withContext(Dispatchers.IO) {
            userDao.insertUser(userEntity)
        }
    }

    suspend fun getUserById(userId: String): UserEntity? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(userId)
        }
    }

    suspend fun clearLocalUser() {
        withContext(Dispatchers.IO) {
            userDao.clearUser()
        }
    }

    /**
     * Mengambil Custom ID ("PeternakXXXX") dari Auth UID.
     */
    fun getCustomUserId(authUid: String, onResult: (customId: String?) -> Unit) {
        userIdMapRef.child(authUid).get().addOnSuccessListener {
            if (it.exists()) {
                onResult(it.getValue(String::class.java))
            } else {
                onResult(null)
            }
        }.addOnFailureListener {
            onResult(null)
        }
    }

    fun listenForUserChanges(userId: String) {
        usersRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.IO).launch {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(UserEntity::class.java)
                        if (user != null) {
                            userDao.insertUser(user)
                        }
                    } else {
                        userDao.deleteUserById(userId)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}