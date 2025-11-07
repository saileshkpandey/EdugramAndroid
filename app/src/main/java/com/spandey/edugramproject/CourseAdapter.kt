package com.spandey.edugramproject

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class CourseAdapter(
    val courses: List<CourseWithProgress>,
    val onClick: (CourseWithProgress) -> Unit
) : RecyclerView.Adapter<CourseAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val card: MaterialCardView = v.findViewById(R.id.courseCard)
        val title: TextView = v.findViewById(R.id.tvCourseTitle)
        val subject: TextView = v.findViewById(R.id.tvCourseSubject)
        val description: TextView = v.findViewById(R.id.tvCourseDescription)
        val hours: TextView = v.findViewById(R.id.tvEstimatedHours)
        val progressBar: ProgressBar = v.findViewById(R.id.progressBar)
        val progressText: TextView = v.findViewById(R.id.tvProgress)
        val status: TextView = v.findViewById(R.id.tvCourseStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val courseWithProgress = courses[position]
        val course = courseWithProgress.course

        holder.title.text = course.title
        holder.subject.text = "${course.subject} • Class ${course.gradeLevel}"
        holder.description.text = course.description
        holder.hours.text = "⏱ ${course.hours}h"

        holder.progressBar.progress = courseWithProgress.progressPercentage
        holder.progressText.text = "${courseWithProgress.progressPercentage}%"

        val statusText = when {
            courseWithProgress.progressPercentage == 0 -> "Not Started"
            courseWithProgress.progressPercentage == 100 -> "Completed"
            else -> "In Progress"
        }
        holder.status.text = statusText

        val statusBgColor = when {
            courseWithProgress.progressPercentage == 0 -> "#E3F2FD" // Light blue
            courseWithProgress.progressPercentage == 100 -> "#E8F5E9" // Light green
            else -> "#FFF3E0"
        }
        val statusTextColor = when {
            courseWithProgress.progressPercentage == 0 -> "#1976D2" // Blue
            courseWithProgress.progressPercentage == 100 -> "#388E3C" // Green
            else -> "#F57C00"
        }
        holder.status.setBackgroundColor(Color.parseColor(statusBgColor))
        holder.status.setTextColor(Color.parseColor(statusTextColor))

        val color = when (course.subject.lowercase()) {
            "mathematics" -> "#1976D2"
            "science" -> "#388E3C"
            "english" -> "#D32F2F"
            else -> "#7B1FA2"
        }
        holder.card.setCardBackgroundColor(Color.parseColor(color))

        holder.card.setOnClickListener {
            onClick(courseWithProgress)
        }
    }

    override fun getItemCount() = courses.size
}

