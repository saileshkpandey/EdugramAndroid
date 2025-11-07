package com.spandey.edugramproject.data

import androidx.room.*

@Dao
interface ProgressDao {
    @Insert
    suspend fun insertProgress(progress: LessonProgressEntity)

    @Query("SELECT * FROM lesson_progress WHERE userId = :uid AND lessonId = :lid")
    suspend fun getProgress(uid: Long, lid: Long): LessonProgressEntity?

    @Query("SELECT * FROM lesson_progress WHERE userId = :uid AND courseId = :cid AND completed = 1")
    suspend fun getCompletedLessons(uid: Long, cid: Long): List<LessonProgressEntity>
}

