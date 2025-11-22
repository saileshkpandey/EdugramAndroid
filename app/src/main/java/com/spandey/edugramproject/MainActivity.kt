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
        // ---------------------- COURSES ----------------------
        db?.courseDao()?.insertCourse(CourseEntity(1, "Mathematics Class 10", "Master algebra, geometry, and trigonometry", "Mathematics", 10, 40))
        db?.courseDao()?.insertCourse(CourseEntity(2, "Science Class 10", "Explore physics, chemistry, and biology", "Science", 10, 35))
        db?.courseDao()?.insertCourse(CourseEntity(3, "English Class 10", "Improve grammar and writing", "English", 10, 30))

// ---------------------- LESSONS ----------------------
// MATH (Course 1)
        db?.lessonDao()?.insertLesson(LessonEntity(1, 1, "Introduction to Real Numbers", "Real numbers include all rational and irrational numbers.", 1, 10))
        db?.lessonDao()?.insertLesson(LessonEntity(2, 1, "Euclid's Division Algorithm", "Learn about Euclid's division algorithm and HCF.", 2, 15))
        db?.lessonDao()?.insertLesson(LessonEntity(3, 1, "Fundamental Theorem of Arithmetic", "Every composite number can be expressed as product of primes.", 3, 15))
        db?.lessonDao()?.insertLesson(LessonEntity(4, 1, "Polynomials", "A polynomial is an expression of variables and coefficients.", 4, 20))
        db?.lessonDao()?.insertLesson(LessonEntity(5, 1, "Linear Equations", "Solve linear equations in two variables.", 5, 20))

// SCIENCE (Course 2)
        db?.lessonDao()?.insertLesson(LessonEntity(6, 2, "Chemical Reactions and Equations", "Learn about chemical reactions.", 1, 10))
        db?.lessonDao()?.insertLesson(LessonEntity(7, 2, "Acids, Bases and Salts", "Properties of acids and bases.", 2, 15))
        db?.lessonDao()?.insertLesson(LessonEntity(8, 2, "Reflection of Light", "Laws of reflection and mirrors.", 3, 15))

// ENGLISH (Course 3)
        db?.lessonDao()?.insertLesson(LessonEntity(9, 3, "Nouns and Pronouns", "Learn about nouns and pronouns.", 1, 10))
        db?.lessonDao()?.insertLesson(LessonEntity(10, 3, "Adjectives and Descriptions", "Understand how adjectives describe nouns.", 2, 12))
        db?.lessonDao()?.insertLesson(LessonEntity(11, 3, "Reading Comprehension", "Practice reading passages.", 3, 15))

// ---------------------- QUIZZES ----------------------
// Math quizzes
        db?.quizDao()?.insertQuiz(QuizEntity(1, 1, "Real Numbers Quiz", 30))
        db?.quizDao()?.insertQuiz(QuizEntity(2, 2, "Euclid's Algorithm Quiz", 30))
        db?.quizDao()?.insertQuiz(QuizEntity(4, 3, "Fundamental Theorem of Arithmetic Quiz", 30))

// Science quizzes
        db?.quizDao()?.insertQuiz(QuizEntity(3, 6, "Chemical Reactions Quiz", 30))
        db?.quizDao()?.insertQuiz(QuizEntity(5, 7, "Acids, Bases & Salts Quiz", 30))
        db?.quizDao()?.insertQuiz(QuizEntity(6, 8, "Reflection of Light Quiz", 30))

// English quizzes
        db?.quizDao()?.insertQuiz(QuizEntity(7, 9, "Nouns and Pronouns Quiz", 30))
        db?.quizDao()?.insertQuiz(QuizEntity(8, 10, "Adjectives Quiz", 30))
        db?.quizDao()?.insertQuiz(QuizEntity(9, 11, "Reading Comprehension Quiz", 30))

// ---------------------- QUESTIONS ----------------------

// MATH — Quiz 1 (Real Numbers)
        db?.questionDao()?.insertQuestion(QuestionEntity(1, 1, "Which of the following is a rational number?", "√2", "π", "0.5", "√3", "C", "0.5 is rational", 1))
        db?.questionDao()?.insertQuestion(QuestionEntity(2, 1, "Decimal expansion of a rational number?", "Always non-terminating", "Always terminating", "Terminating or repeating", "Non-terminating non-repeating", "C", "Rational = terminating or repeating", 2))
        db?.questionDao()?.insertQuestion(QuestionEntity(3, 1, "Which property is NOT true for real numbers?", "Closure", "Commutative", "Associative", "All are true", "D", "All are true", 3))

// MATH — Quiz 2 (Euclid)
        db?.questionDao()?.insertQuestion(QuestionEntity(4, 2, "Euclid’s lemma states:", "a = bq + r", "a = bq - r", "a = b + r", "a = bq", "A", "Correct form a=bq+r", 1))
        db?.questionDao()?.insertQuestion(QuestionEntity(5, 2, "HCF of 56 & 88?", "4", "8", "2", "16", "B", "HCF = 8", 2))
        db?.questionDao()?.insertQuestion(QuestionEntity(6, 2, "Remainder range in lemma?", "0 ≤ r < b", "0 < r < b", "0 ≤ r ≤ b", "None", "A", "Correct range", 3))

// MATH — Quiz 3 (FTA)
        db?.questionDao()?.insertQuestion(QuestionEntity(7, 4, "Prime factorization is:", "Unique", "Same for all numbers", "Not unique", "Irrelevant", "A", "Uniqueness theorem", 1))
        db?.questionDao()?.insertQuestion(QuestionEntity(8, 4, "2 × 3 × 5 is factorization of:", "10", "20", "30", "15", "C", "30 = 2×3×5", 2))
        db?.questionDao()?.insertQuestion(QuestionEntity(9, 4, "What are prime numbers?", "Only even numbers", "Numbers with 2 factors", "Numbers with 3 factors", "None", "B", "Prime = 2 factors", 3))

// SCIENCE — Quiz 4 (Chemical Reactions)
        db?.questionDao()?.insertQuestion(QuestionEntity(10, 3, "Balanced equation means:", "Equal atoms both sides", "Mass conserved", "Both A and B", "None", "C", "Correct", 1))
        db?.questionDao()?.insertQuestion(QuestionEntity(11, 3, "Photosynthesis is a:", "Combination", "Decomposition", "Displacement", "Redox", "A", "Combination reaction", 2))
        db?.questionDao()?.insertQuestion(QuestionEntity(12, 3, "Mg + O₂ → ?", "MgO", "MgO₂", "Mg₂O", "O₂Mg", "A", "Forms magnesium oxide", 3))

// SCIENCE — Quiz 5 (Acids & Bases)
        db?.questionDao()?.insertQuestion(QuestionEntity(13, 5, "pH of neutral solution?", "0", "5", "7", "14", "C", "Neutral = 7", 1))
        db?.questionDao()?.insertQuestion(QuestionEntity(14, 5, "Acid gives:", "OH⁻", "H⁺", "H₂", "O₂", "B", "Acids release H+", 2))
        db?.questionDao()?.insertQuestion(QuestionEntity(15, 5, "Base tastes:", "Sweet", "Bitter", "Sour", "Salty", "B", "Bitter taste", 3))

// SCIENCE — Quiz 6 (Reflection of Light)
        db?.questionDao()?.insertQuestion(QuestionEntity(16, 6, "Angle of incidence =", "Angle of reflection", "90°", "0°", "180°", "A", "Law of reflection", 1))
        db?.questionDao()?.insertQuestion(QuestionEntity(17, 6, "Mirror used for shaving?", "Concave", "Convex", "Plane", "None", "A", "Magnification needed", 2))
        db?.questionDao()?.insertQuestion(QuestionEntity(18, 6, "Image in plane mirror is:", "Real", "Virtual", "Inverted", "Both", "B", "Always virtual", 3))

// ENGLISH — Quiz 7 (Nouns & Pronouns)
        db?.questionDao()?.insertQuestion(QuestionEntity(19, 7, "A noun is:", "Action word", "Name of person/place/thing", "Joining word", "Describing word", "B", "Definition of noun", 1))
        db?.questionDao()?.insertQuestion(QuestionEntity(20, 7, "He/She/They are:", "Nouns", "Adjectives", "Pronouns", "Verbs", "C", "Pronoun category", 2))
        db?.questionDao()?.insertQuestion(QuestionEntity(21, 7, "Choose pronoun: ___ is my book.", "This", "He", "John", "Tall", "B", "He fits the blank", 3))

// ENGLISH — Quiz 8 (Adjectives)
        db?.questionDao()?.insertQuestion(QuestionEntity(22, 8, "Adjectives describe:", "Verbs", "Nouns", "Pronouns", "Adverbs", "B", "Adjective → noun", 1))
        db?.questionDao()?.insertQuestion(QuestionEntity(23, 8, "Select adjective:", "Blue", "Run", "Quickly", "Him", "A", "Color = adjective", 2))
        db?.questionDao()?.insertQuestion(QuestionEntity(24, 8, "Adjective in sentence: She is a *smart* girl.", "She", "Is", "Smart", "Girl", "C", "Smart describes girl", 3))

// ENGLISH — Quiz 9 (Reading comprehension)
        db?.questionDao()?.insertQuestion(QuestionEntity(25, 9, "Comprehension means:", "Writing", "Reading & understanding", "Speaking", "Memorizing", "B", "Correct meaning", 1))
        db?.questionDao()?.insertQuestion(QuestionEntity(26, 9, "Purpose of reading?", "Just finish", "Understand meaning", "See pictures", "Skip text", "B", "To understand", 2))
        db?.questionDao()?.insertQuestion(QuestionEntity(27, 9, "Good readers:", "Read slowly", "Guess answers", "Understand context", "Skip words", "C", "Context matters", 3))

    }
}
