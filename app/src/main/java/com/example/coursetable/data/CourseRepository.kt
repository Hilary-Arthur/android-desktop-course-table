package com.example.coursetable.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CourseRepository(private val context: Context) {
    private val gson = Gson()
    private var allCourses: List<Course> = emptyList()

    fun loadCourses(): List<Course> {
        if (allCourses.isEmpty()) {
            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier("course", "raw", context.packageName)
            )
            val json = inputStream.bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Course>>() {}.type
            allCourses = gson.fromJson(json, type)
        }
        return allCourses
    }

    fun getCoursesForWeek(week: Int): List<Course> {
        return loadCourses().filter { course ->
            com.example.coursetable.util.WeekParser.isWeekActive(course.weeks, week)
        }
    }
}
