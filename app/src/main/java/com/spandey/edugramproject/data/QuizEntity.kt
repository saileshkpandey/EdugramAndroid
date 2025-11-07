package com.spandey.edugramproject.data

import androidx.room.*

@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey
    val id: Long,
    val lessonId: Long,
    val title: String,
    val passingScore: Int
)
