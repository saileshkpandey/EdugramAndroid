package com.spandey.edugramproject.data

import androidx.room.*

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey
    val id: Long,
    val quizId: Long,
    val text: String,
    val optA: String,
    val optB: String,
    val optC: String,
    val optD: String,
    val answer: String,
    val explanation: String,
    val order: Int
)
