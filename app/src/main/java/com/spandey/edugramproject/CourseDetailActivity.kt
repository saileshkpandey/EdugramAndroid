package com.spandey.edugramproject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.spandey.edugramproject.data.CourseEntity
import com.spandey.edugramproject.data.LessonEntity
import com.spandey.edugramproject.data.LessonProgressEntity
import kotlinx.coroutines.launch

class CourseDetailActivity : AppCompatActivity() {

    var db: AppDatabase? = null
    var courseId: Long = 0
    var userId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)

        courseId = intent.getLongExtra("COURSE_ID", 0)
        db = AppDatabase.getDB(this)

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    fun loadData() {
        lifecycleScope.launch {
            val user = db?.userDao()?.getUser()
            userId = user?.id ?: 0

            val course = db?.courseDao()?.getCourseById(courseId)
            val lessons = db?.lessonDao()?.getLessonsByCourse(courseId) ?: listOf()
            val completed = db?.progressDao()?.getCompletedLessons(userId, courseId) ?: listOf()

            runOnUiThread {
                course?.let {
                    val color = when (it.subject.lowercase()) {
                        "mathematics" -> "#1976D2"
                        "science" -> "#388E3C"
                        "english" -> "#D32F2F"
                        else -> "#7B1FA2"
                    }
                    findViewById<LinearLayout>(R.id.courseHeaderBg).setBackgroundColor(Color.parseColor(color))

                    findViewById<TextView>(R.id.tvCourseTitle).text = it.title
                    findViewById<TextView>(R.id.tvCourseSubject).text = "${it.subject} • Class ${it.gradeLevel}"
                    findViewById<TextView>(R.id.tvCourseDescription).text = it.description
                    findViewById<TextView>(R.id.tvEstimatedHours).text = it.hours.toString()

                    val progress = if (lessons.isEmpty()) 0 else (completed.size * 100) / lessons.size
                    findViewById<TextView>(R.id.tvCompletionPercentage).text = "$progress%"
                }

                val btnStart = findViewById<MaterialButton>(R.id.btnStartCourse)
                btnStart.setOnClickListener {
                    if (lessons.isNotEmpty()) {
                        val i = Intent(this@CourseDetailActivity, LessonActivity::class.java)
                        i.putExtra("LESSON_ID", lessons[0].id)
                        i.putExtra("COURSE_ID", courseId)
                        startActivity(i)
                    }
                }

                val rv = findViewById<RecyclerView>(R.id.rvLessons)
                rv.layoutManager = LinearLayoutManager(this@CourseDetailActivity)
                val adapter = LessonAdapter(lessons, completed.map { it.lessonId }) { lesson, isUnlocked ->
                    if (isUnlocked) {
                        val i = Intent(this@CourseDetailActivity, LessonActivity::class.java)
                        i.putExtra("LESSON_ID", lesson.id)
                        i.putExtra("COURSE_ID", courseId)
                        startActivity(i)
                    } else {
                        android.widget.Toast.makeText(
                            this@CourseDetailActivity,
                            "🔒 Complete the previous lesson first!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                rv.adapter = adapter
            }
        }
    }
}

