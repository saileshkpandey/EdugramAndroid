package com.spandey.edugramproject.data

import androidx.room.*

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val xp: Int,
    val level: Int
)
