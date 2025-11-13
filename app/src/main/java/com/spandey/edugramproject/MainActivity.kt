package com.spandey.edugramproject

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.spandey.edugramproject.data.CourseEntity
import com.spandey.edugramproject.data.LessonEntity
import com.spandey.edugramproject.data.QuizEntity
import com.spandey.edugramproject.data.QuestionEntity
import com.spandey.edugramproject.data.UserEntity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    var db: AppDatabase? = null
    var welcomeText: TextView? = null
    var xpText: TextView? = null
    var levelText: TextView? = null
    var badgeText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // open ChatbotActivity when floating button clicked
        val askDoubtFab = findViewById<FloatingActionButton>(R.id.btnAskDoubt)
        askDoubtFab.setOnClickListener {
            startActivity(Intent(this, ChatbotActivity::class.java))
        }

        db = AppDatabase.getDB(this)

        welcomeText = findViewById(R.id.tvWelcome)
        xpText = findViewById(R.id.tvXpValue)
        levelText = findViewById(R.id.tvLevelValue)
        badgeText = findViewById(R.id.tvBadgeCount)

        findViewById<MaterialButton>(R.id.btnBrowseCourses).setOnClickListener {
            val intent = Intent(this, CourseListActivity::class.java)
            startActivity(intent)
        }

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        if (!prefs.getBoolean("seeded", false)) {
            lifecycleScope.launch {
                seedData()
                prefs.edit().putBoolean("seeded", true).apply()
            }
        }

        loadUserData()
    }

    fun loadUserData() {
        lifecycleScope.launch {
            var user = db?.userDao()?.getUser()
            if (user == null) {
                val id = db?.userDao()?.insertUser(UserEntity(0, "Student", 0, 1))
                user = db?.userDao()?.getUserById(id!!)
            }

            runOnUiThread {
                welcomeText?.text = "Welcome, ${user?.name}!"
                xpText?.text = user?.xp.toString()
                levelText?.text = user?.level.toString()
                badgeText?.text = "0"
            }
        }
    }

    suspend fun seedData() {
        // seed courses
        db?.courseDao()?.insertCourse(CourseEntity(1, "Mathematics Class 10", "Master algebra, geometry, and trigonometry", "Mathematics", 10, 40))
        db?.courseDao()?.insertCourse(CourseEntity(2, "Science Class 10", "Explore physics, chemistry, and biology", "Science", 10, 35))
        db?.courseDao()?.insertCourse(CourseEntity(3, "English Class 10", "Improve grammar and writing", "English", 10, 30))

        db?.lessonDao()?.insertLesson(LessonEntity(1, 1, "Introduction to Real Numbers", "Real numbers include all rational and irrational numbers. In this lesson, we'll explore the number system.", 1, 10))
        db?.lessonDao()?.insertLesson(LessonEntity(2, 1, "Euclid's Division Algorithm", "Learn about Euclid's division algorithm and HCF.", 2, 15))
        db?.lessonDao()?.insertLesson(LessonEntity(3, 1, "Fundamental Theorem of Arithmetic", "Every composite number can be expressed as product of primes.", 3, 15))
        db?.lessonDao()?.insertLesson(LessonEntity(4, 1, "Polynomials", "A polynomial is an expression of variables and coefficients.", 4, 20))
        db?.lessonDao()?.insertLesson(LessonEntity(5, 1, "Linear Equations", "Solve linear equations in two variables.", 5, 20))

        db?.lessonDao()?.insertLesson(LessonEntity(6, 2, "Chemical Reactions and Equations", "Learn about chemical reactions and balancing equations.", 1, 10))
        db?.lessonDao()?.insertLesson(LessonEntity(7, 2, "Acids, Bases and Salts", "Properties of acids and bases, pH scale.", 2, 15))
        db?.lessonDao()?.insertLesson(LessonEntity(8, 2, "Reflection of Light", "Laws of reflection and image formation by mirrors.", 3, 15))

        db?.quizDao()?.insertQuiz(QuizEntity(1, 1, "Real Numbers Quiz", 30))
        db?.quizDao()?.insertQuiz(QuizEntity(2, 2, "Euclid's Algorithm Quiz", 30))
        db?.quizDao()?.insertQuiz(QuizEntity(3, 6, "Chemical Reactions Quiz", 30))

        db?.questionDao()?.insertQuestion(QuestionEntity(1, 1, "Which of the following is a rational number?", "√2", "π", "0.5", "√3", "C", "0.5 = 1/2", 1))
        db?.questionDao()?.insertQuestion(QuestionEntity(2, 1, "What is decimal expansion of rational number?", "Always non-terminating", "Always terminating", "Terminating or non-terminating repeating", "Non-terminating non-repeating", "C", "Rational numbers are terminating or repeating.", 2))
        db?.questionDao()?.insertQuestion(QuestionEntity(3, 1, "Which property is NOT true for real numbers?", "Closure", "Commutative", "Associative", "All are true", "D", "All properties are true.", 3))

        db?.questionDao()?.insertQuestion(QuestionEntity(4, 2, "What does Euclid's division lemma state?", "a = bq + r, where 0 ≤ r < b", "a = bq - r", "a = b + r", "a = bq", "A", "Euclid's lemma: a = bq + r", 1))
        db?.questionDao()?.insertQuestion(QuestionEntity(5, 2, "HCF of 56 and 88 is:", "4", "8", "2", "16", "B", "Using Euclid's algorithm: HCF = 8", 2))

        db?.questionDao()?.insertQuestion(QuestionEntity(6, 3, "A chemical equation is balanced when:", "Atoms equal on both sides", "Mass is conserved", "Both A and B", "None", "C", "Equal atoms and mass conserved.", 1))
        db?.questionDao()?.insertQuestion(QuestionEntity(7, 3, "Which type of reaction is photosynthesis?", "Combination", "Decomposition", "Displacement", "Redox", "A", "Photosynthesis combines CO2 and H2O.", 2))
    }
}
