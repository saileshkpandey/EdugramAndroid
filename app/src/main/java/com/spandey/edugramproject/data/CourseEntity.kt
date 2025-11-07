package com.spandey.edugramproject.data

import androidx.room.*

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val description: String,
    val subject: String,
    val gradeLevel: Int,
    val hours: Int
)
