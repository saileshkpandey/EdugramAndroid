package com.spandey.edugramproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.spandey.edugramproject.data.LessonEntity

class LessonAdapter(
    val lessons: List<LessonEntity>,
    val completedIds: List<Long>,
    val onClick: (LessonEntity, Boolean) -> Unit
) : RecyclerView.Adapter<LessonAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val card: MaterialCardView = v.findViewById(R.id.lessonCard)
        val number: TextView = v.findViewById(R.id.tvLessonNumber)
        val title: TextView = v.findViewById(R.id.tvLessonTitle)
        val xp: TextView = v.findViewById(R.id.tvLessonXp)
        val status: TextView = v.findViewById(R.id.tvLessonStatus)
    }

    private fun isLessonUnlocked(position: Int): Boolean {
        if (position == 0) return true

        val previousLesson = lessons[position - 1]
        return completedIds.contains(previousLesson.id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lesson = lessons[position]
        val isUnlocked = isLessonUnlocked(position)
        val isCompleted = completedIds.contains(lesson.id)

        holder.number.text = lesson.orderNum.toString()
        holder.title.text = lesson.title
        holder.xp.text = "⭐ +${lesson.xpReward} XP"

        when {
            isCompleted -> {
                holder.status.text = "✅"
                holder.card.alpha = 1.0f
                holder.card.isEnabled = true
            }
            isUnlocked -> {
                holder.status.text = "🔘"
                holder.card.alpha = 1.0f
                holder.card.isEnabled = true
            }
            else -> {
                holder.status.text = "🔒"
                holder.card.alpha = 0.5f
                holder.card.isEnabled = false
            }
        }

        holder.card.setOnClickListener {
            onClick(lesson, isUnlocked)
        }
    }

    override fun getItemCount() = lessons.size
}

