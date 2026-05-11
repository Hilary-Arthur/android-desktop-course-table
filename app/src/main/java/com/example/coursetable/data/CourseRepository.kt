package com.example.coursetable.data

import android.content.Context
import com.example.coursetable.BuildConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CourseRepository(private val context: Context) {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("course_prefs", Context.MODE_PRIVATE)
    private var allCourses: List<Course> = emptyList()
    private var courseTimes: List<CourseTime> = emptyList()

    fun getSchoolName(): String = BuildConfig.SCHOOL_NAME

    fun loadCourses(): List<Course> {
        if (allCourses.isEmpty()) {
            val importedJson = prefs.getString(KEY_IMPORTED_COURSES, null)
            if (importedJson != null) {
                val type = object : TypeToken<List<Course>>() {}.type
                allCourses = gson.fromJson(importedJson, type)
            } else {
                val inputStream = context.resources.openRawResource(
                    context.resources.getIdentifier(BuildConfig.COURSE_FILE, "raw", context.packageName)
                )
                val json = inputStream.bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<Course>>() {}.type
                allCourses = gson.fromJson(json, type)
            }
        }
        return allCourses
    }

    fun loadCourseTimes(): List<CourseTime> {
        if (courseTimes.isEmpty()) {
            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier("course_time", "raw", context.packageName)
            )
            val json = inputStream.bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<CourseTime>>>() {}.type
            val timeMap: Map<String, List<CourseTime>> = gson.fromJson(json, type)
            courseTimes = timeMap[BuildConfig.SCHOOL_ID] ?: emptyList()
        }
        return courseTimes
    }

    fun getCoursesForWeek(week: Int): List<Course> {
        return loadCourses().filter { course ->
            com.example.coursetable.util.WeekParser.isWeekActive(course.weeks, week)
        }
    }

    fun hasImportedCourses(): Boolean = prefs.contains(KEY_IMPORTED_COURSES)

    fun saveImportedCourses(json: String): List<Course> {
        val type = object : TypeToken<List<Course>>() {}.type
        val courses: List<Course> = gson.fromJson(json, type)
        prefs.edit().putString(KEY_IMPORTED_COURSES, json).apply()
        allCourses = courses
        return courses
    }

    fun clearImportedCourses() {
        prefs.edit().remove(KEY_IMPORTED_COURSES).apply()
        allCourses = emptyList()
    }

    fun getCourseNames(): List<String> {
        return loadCourses().map { it.name }.distinct().sorted()
    }

    fun loadExams(): List<Exam> {
        val json = prefs.getString(KEY_EXAMS, null) ?: return emptyList()
        val type = object : TypeToken<List<Exam>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveExam(exam: Exam) {
        val exams = loadExams().toMutableList()
        exams.add(exam)
        prefs.edit().putString(KEY_EXAMS, gson.toJson(exams)).apply()
    }

    fun deleteExam(id: Long) {
        val exams = loadExams().filter { it.id != id }
        prefs.edit().putString(KEY_EXAMS, gson.toJson(exams)).apply()
    }

    fun getSemesterStart(): Long? {
        val millis = prefs.getLong(KEY_SEMESTER_START, -1L)
        return if (millis > 0) millis else null
    }

    fun setSemesterStart(dateMillis: Long) {
        prefs.edit().putLong(KEY_SEMESTER_START, dateMillis).apply()
    }

    companion object {
        private const val KEY_IMPORTED_COURSES = "imported_courses"
        private const val KEY_EXAMS = "exams"
        private const val KEY_SEMESTER_START = "semester_start"
    }
}
