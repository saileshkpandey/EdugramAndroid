package com.spandey.edugramproject.data

import androidx.room.*

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey
    val id: Long,
    val courseId: Long,
    val title: String,
    val content: String,
    val orderNum: Int,
    val xpReward: Int
)
