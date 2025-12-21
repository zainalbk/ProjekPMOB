package com.example.proyekinotekai.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    // Simpan atau update data pengguna.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Ambil data pengguna berdasarkan ID kustom ("PeternakXXXX").
    @Query("SELECT * FROM user_table WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    // Hapus pengguna spesifik berdasarkan ID kustom.
    @Query("DELETE FROM user_table WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    // Hapus semua data dari tabel pengguna.
    @Query("DELETE FROM user_table")
    suspend fun clearUser()
}
