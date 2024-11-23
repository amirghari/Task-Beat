package com.example.taskbeat.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskbeat.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user WHERE email = :email")
    fun getUserByEmail(email: String): Flow<User?>

    @Query("DELETE FROM user WHERE email = :email")
    suspend fun deleteUserByEmail(email: String)

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<User>>
}