package com.spandey.edugramproject

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.spandey.edugramproject.data.UserEntity
import com.spandey.edugramproject.data.UserDao
import com.spandey.edugramproject.data.CourseEntity
import com.spandey.edugramproject.data.CourseDao
import com.spandey.edugramproject.data.LessonEntity
import com.spandey.edugramproject.data.LessonDao
import com.spandey.edugramproject.data.QuizEntity
import com.spandey.edugramproject.data.QuizDao
import com.spandey.edugramproject.data.QuestionEntity
import com.spandey.edugramproject.data.QuestionDao
import com.spandey.edugramproject.data.LessonProgressEntity
import com.spandey.edugramproject.data.ProgressDao

@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class,
        LessonEntity::class,
        QuizEntity::class,
        QuestionEntity::class,
        LessonProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun lessonDao(): LessonDao
    abstract fun quizDao(): QuizDao
    abstract fun questionDao(): QuestionDao
    abstract fun progressDao(): ProgressDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDB(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "edugram_db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
        }
    }
}

