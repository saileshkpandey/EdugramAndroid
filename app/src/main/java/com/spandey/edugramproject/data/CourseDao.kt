package com.spandey.edugramproject.data

import androidx.room.*

@Dao
interface CourseDao {
    @Insert
    suspend fun insertCourse(course: CourseEntity)

    @Query("SELECT * FROM courses")
    suspend fun getAllCourses(): List<CourseEntity>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: Long): CourseEntity?
}
