package com.spandey.edugramproject.data

import androidx.room.*

@Dao
interface QuestionDao {
    @Insert
    suspend fun insertQuestion(question: QuestionEntity)

    @Query("SELECT * FROM questions WHERE quizId = :qId ORDER BY `order`")
    suspend fun getQuestionsByQuiz(qId: Long): List<QuestionEntity>
}
