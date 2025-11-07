package com.spandey.edugramproject.data

import androidx.room.*

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val lessonId: Long,
    val courseId: Long,
    val completed: Boolean
)
