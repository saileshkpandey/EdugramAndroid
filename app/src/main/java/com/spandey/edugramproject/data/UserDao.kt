package com.spandey.edugramproject.data

import androidx.room.*

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("UPDATE users SET xp = xp + :points WHERE id = :userId")
    suspend fun addXP(userId: Long, points: Int)
}

