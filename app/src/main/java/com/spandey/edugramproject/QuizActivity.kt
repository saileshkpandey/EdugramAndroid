package com.spandey.edugramproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.spandey.edugramproject.data.QuestionEntity
import com.spandey.edugramproject.data.LessonEntity
import com.spandey.edugramproject.data.LessonProgressEntity
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {

    var db: AppDatabase? = null
    var quizId: Long = 0
    var lessonId: Long = 0
    var courseId: Long = 0
    var userId: Long = 0

    var questions = listOf<QuestionEntity>()
    var currentIndex = 0
    var answers = mutableMapOf<Long, String>()
    var allLessons = listOf<LessonEntity>()

    lateinit var quizView: ScrollView
    lateinit var resultView: LinearLayout
    lateinit var quizTitle: TextView
    lateinit var questionCount: TextView
    lateinit var passingScoreText: TextView
    lateinit var questionText: TextView
    lateinit var radioGroup: RadioGroup
    lateinit var radioA: RadioButton
    lateinit var radioB: RadioButton
    lateinit var radioC: RadioButton
    lateinit var radioD: RadioButton
    lateinit var prevBtn: MaterialButton
    lateinit var nextBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        quizId = intent.getLongExtra("QUIZ_ID", 0)
        lessonId = intent.getLongExtra("LESSON_ID", 0)
        courseId = intent.getLongExtra("COURSE_ID", 0)

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        setupViews()
        db = AppDatabase.getDB(this)
        loadQuiz()
    }

    fun setupViews() {
        quizView = findViewById(R.id.quizContent)
        resultView = findViewById(R.id.resultsScreen)
        quizTitle = findViewById(R.id.tvQuizTitle)
        questionCount = findViewById(R.id.tvQuestionCounter)
        passingScoreText = findViewById(R.id.tvPassingScore)
        questionText = findViewById(R.id.tvQuestionText)
        radioGroup = findViewById(R.id.radioGroupOptions)
        radioA = findViewById(R.id.radioOptionA)
        radioB = findViewById(R.id.radioOptionB)
        radioC = findViewById(R.id.radioOptionC)
        radioD = findViewById(R.id.radioOptionD)
        prevBtn = findViewById(R.id.btnPrevious)
        nextBtn = findViewById(R.id.btnNext)

        prevBtn.setOnClickListener { prev() }
        nextBtn.setOnClickListener { next() }
        radioGroup.setOnCheckedChangeListener { _, _ -> saveAnswer() }
    }

    fun loadQuiz() {
        lifecycleScope.launch {
            val user = db?.userDao()?.getUser()
            userId = user?.id ?: 0

            val quiz = db?.quizDao()?.getQuizById(quizId)
            questions = db?.questionDao()?.getQuestionsByQuiz(quizId) ?: listOf()
            allLessons = db?.lessonDao()?.getLessonsByCourse(courseId) ?: listOf()

            runOnUiThread {
                quiz?.let {
                    quizTitle.text = it.title
                    passingScoreText.text = "Passing: ${it.passingScore}%"
                }
                if (questions.isNotEmpty()) {
                    showQuestion(0)
                }
            }
        }
    }

    fun showQuestion(idx: Int) {
        if (idx < 0 || idx >= questions.size) return

        val q = questions[idx]
        currentIndex = idx

        questionCount.text = "Question ${idx + 1}/${questions.size}"
        questionText.text = q.text
        radioA.text = "A. ${q.optA}"
        radioB.text = "B. ${q.optB}"
        radioC.text = "C. ${q.optC}"
        radioD.text = "D. ${q.optD}"

        radioGroup.clearCheck()
        answers[q.id]?.let { ans ->
            when (ans) {
                "A" -> radioA.isChecked = true
                "B" -> radioB.isChecked = true
                "C" -> radioC.isChecked = true
                "D" -> radioD.isChecked = true
            }
        }

        prevBtn.isEnabled = idx > 0
        nextBtn.text = if (idx == questions.size - 1) "Submit Quiz" else "Next"
    }

    fun saveAnswer() {
        val q = questions[currentIndex]
        val ans = when (radioGroup.checkedRadioButtonId) {
            radioA.id -> "A"
            radioB.id -> "B"
            radioC.id -> "C"
            radioD.id -> "D"
            else -> null
        }
        ans?.let { answers[q.id] = it }
    }

    fun prev() {
        if (currentIndex > 0) {
            showQuestion(currentIndex - 1)
        }
    }

    fun next() {
        if (currentIndex < questions.size - 1) {
            showQuestion(currentIndex + 1)
        } else {
            submit()
        }
    }

    fun submit() {
        var correct = 0
        questions.forEach { q ->
            if (answers[q.id] == q.answer) {
                correct++
            }
        }

        val score = if (questions.isNotEmpty()) (correct * 100) / questions.size else 0

        lifecycleScope.launch {
            val quiz = db?.quizDao()?.getQuizById(quizId)
            val lesson = db?.lessonDao()?.getLessonById(lessonId)

            val passed = score >= (quiz?.passingScore ?: 70)
            val xp = if (passed) (lesson?.xpReward ?: 0) + 20 else 0

            if (passed) {
                db?.userDao()?.addXP(userId, xp)
                val existing = db?.progressDao()?.getProgress(userId, lessonId)
                if (existing == null) {
                    db?.progressDao()?.insertProgress(
                        LessonProgressEntity(0, userId, lessonId, courseId, true)
                    )
                }
            }

            runOnUiThread {
                showResults(score, passed, xp)
            }
        }
    }

    fun showResults(score: Int, passed: Boolean, xp: Int) {
        quizView.visibility = View.GONE
        resultView.visibility = View.VISIBLE

        val emoji = findViewById<TextView>(R.id.tvResultEmoji)
        val title = findViewById<TextView>(R.id.tvResultTitle)
        val scoreText = findViewById<TextView>(R.id.tvResultScore)
        val xpText = findViewById<TextView>(R.id.tvXpEarned)

        if (passed) {
            emoji.text = "🎉"
            title.text = "Congratulations!"
            title.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            emoji.text = "📚"
            title.text = "Keep Learning!"
            title.setTextColor(getColor(android.R.color.holo_orange_dark))
        }

        scoreText.text = "You scored $score%"
        xpText.text = if (xp > 0) "⭐ +$xp XP Earned" else "Try again"

        val backBtn = findViewById<MaterialButton>(R.id.btnReturnToCourse)
        val retryBtn = findViewById<MaterialButton>(R.id.btnRetryQuiz)

        if (passed) {
            val idx = allLessons.indexOfFirst { it.id == lessonId }
            if (idx >= 0 && idx < allLessons.size - 1) {
                backBtn.text = "Next Lesson"
                backBtn.setOnClickListener {
                    val i = Intent(this, LessonActivity::class.java)
                    i.putExtra("LESSON_ID", allLessons[idx + 1].id)
                    i.putExtra("COURSE_ID", courseId)
                    startActivity(i)
                    finish()
                }
            } else {
                backBtn.text = "Back to Course"
                backBtn.setOnClickListener { finish() }
            }
            retryBtn.visibility = View.GONE
        } else {
            backBtn.text = "Back to Course"
            backBtn.setOnClickListener { finish() }
            retryBtn.setOnClickListener {
                currentIndex = 0
                answers.clear()
                resultView.visibility = View.GONE
                quizView.visibility = View.VISIBLE
                showQuestion(0)
            }
        }
    }
}

