package com.example.proyekinotekai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey
    val id: String = "",
    val email: String = "",
    val nama: String = "",
    val noTelp: String = "",
    val alamat: String = "",
    val tanggalLahir: String? = null,
    val profilePictureUrl: String? = null
)