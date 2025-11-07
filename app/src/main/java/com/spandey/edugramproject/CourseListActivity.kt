package com.spandey.edugramproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class CourseListActivity : AppCompatActivity() {

    var db: AppDatabase? = null
    var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        db = AppDatabase.getDB(this)
        recyclerView = findViewById(R.id.rvCourses)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        loadCourses()
    }

    override fun onResume() {
        super.onResume()
        loadCourses()
    }

    fun loadCourses() {
        lifecycleScope.launch {
            val courses = db?.courseDao()?.getAllCourses() ?: listOf()
            val user = db?.userDao()?.getUser()
            val userId = user?.id ?: 0

            val coursesWithProgress = courses.map { course ->
                val lessons = db?.lessonDao()?.getLessonsByCourse(course.id) ?: listOf()
                val completedLessons = db?.progressDao()?.getCompletedLessons(userId, course.id) ?: listOf()
                val progress = if (lessons.isEmpty()) 0 else (completedLessons.size * 100) / lessons.size

                CourseWithProgress(course, progress, completedLessons.size, lessons.size)
            }

            runOnUiThread {
                val adapter = CourseAdapter(coursesWithProgress) { courseWithProgress ->
                    val i = Intent(this@CourseListActivity, CourseDetailActivity::class.java)
                    i.putExtra("COURSE_ID", courseWithProgress.course.id)
                    startActivity(i)
                }
                recyclerView?.adapter = adapter
            }
        }
    }
}

data class CourseWithProgress(
    val course: com.spandey.edugramproject.data.CourseEntity,
    val progressPercentage: Int,
    val completedLessons: Int,
    val totalLessons: Int
)

