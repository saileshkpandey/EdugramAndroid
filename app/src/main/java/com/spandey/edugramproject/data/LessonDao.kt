package com.spandey.edugramproject.data

import androidx.room.*

@Dao
interface LessonDao {
    @Insert
    suspend fun insertLesson(lesson: LessonEntity)

    @Query("SELECT * FROM lessons WHERE courseId = :cId ORDER BY orderNum")
    suspend fun getLessonsByCourse(cId: Long): List<LessonEntity>

    @Query("SELECT * FROM lessons WHERE id = :lessonId")
    suspend fun getLessonById(lessonId: Long): LessonEntity?
}
