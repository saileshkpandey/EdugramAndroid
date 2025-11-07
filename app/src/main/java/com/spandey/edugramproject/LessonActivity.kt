package com.spandey.edugramproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class LessonActivity : AppCompatActivity() {

    var db: AppDatabase? = null
    var lessonId: Long = 0
    var courseId: Long = 0
    var userId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)

        lessonId = intent.getLongExtra("LESSON_ID", 0)
        courseId = intent.getLongExtra("COURSE_ID", 0)

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        db = AppDatabase.getDB(this)

        findViewById<MaterialButton>(R.id.btnTakeQuiz).setOnClickListener {
            goToQuiz()
        }

        loadLesson()
    }

    fun loadLesson() {
        lifecycleScope.launch {
            val user = db?.userDao()?.getUser()
            userId = user?.id ?: 0

            val lesson = db?.lessonDao()?.getLessonById(lessonId)
            val progress = db?.progressDao()?.getProgress(userId, lessonId)

            val allLessons = db?.lessonDao()?.getLessonsByCourse(courseId) ?: listOf()
            val completedLessons = db?.progressDao()?.getCompletedLessons(userId, courseId) ?: listOf()
            val completedIds = completedLessons.map { it.lessonId }

            val isLessonUnlocked = lesson?.let { currentLesson ->
                val lessonIndex = allLessons.indexOfFirst { it.id == currentLesson.id }
                if (lessonIndex == 0) {
                    true
                } else if (lessonIndex > 0) {
                    val previousLesson = allLessons[lessonIndex - 1]
                    completedIds.contains(previousLesson.id)
                } else {
                    false
                }
            } ?: false

            runOnUiThread {
                if (!isLessonUnlocked) {
                    Toast.makeText(this@LessonActivity, "🔒 This lesson is locked. Complete the previous lesson first!", Toast.LENGTH_LONG).show()
                    finish()
                    return@runOnUiThread
                }

                lesson?.let {
                    findViewById<TextView>(R.id.tvLessonBadge).text = "Lesson ${it.orderNum}"
                    findViewById<TextView>(R.id.tvLessonTitle).text = it.title
                    findViewById<TextView>(R.id.tvLessonContent).text = it.content

                    val btnComplete = findViewById<MaterialButton>(R.id.btnMarkComplete)
                    val tvXp = findViewById<TextView>(R.id.tvXpReward)

                    if (progress?.completed == true) {
                        btnComplete.text = "Completed ✅"
                        btnComplete.isEnabled = false
                        tvXp.text = "🎉 Lesson completed!"
                    } else {
                        btnComplete.visibility = View.GONE
                        tvXp.text = "⭐ Complete quiz to earn ${it.xpReward} XP"
                    }
                }
            }
        }
    }

    fun goToQuiz() {
        lifecycleScope.launch {
            val quiz = db?.quizDao()?.getQuizByLesson(lessonId)
            runOnUiThread {
                if (quiz != null) {
                    val i = Intent(this@LessonActivity, QuizActivity::class.java)
                    i.putExtra("QUIZ_ID", quiz.id)
                    i.putExtra("LESSON_ID", lessonId)
                    i.putExtra("COURSE_ID", courseId)
                    startActivity(i)
                } else {
                    Toast.makeText(this@LessonActivity, "No quiz available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
