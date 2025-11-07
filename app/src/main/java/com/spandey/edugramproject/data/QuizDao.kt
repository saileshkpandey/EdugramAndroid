package com.spandey.edugramproject.data

import androidx.room.*

@Dao
interface QuizDao {
    @Insert
    suspend fun insertQuiz(quiz: QuizEntity)

    @Query("SELECT * FROM quizzes WHERE lessonId = :lId")
    suspend fun getQuizByLesson(lId: Long): QuizEntity?

    @Query("SELECT * FROM quizzes WHERE id = :quizId")
    suspend fun getQuizById(quizId: Long): QuizEntity?
}
