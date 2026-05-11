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
    private var gradePointTable: Map<String, List<GradePointRange>>? = null

    fun getSchoolName(): String = BuildConfig.SCHOOL_NAME

    fun loadCourses(): List<Course> {
        if (allCourses.isEmpty()) {
            val importedJson = prefs.getString(KEY_IMPORTED_COURSES, null)
            if (importedJson != null) {
                try {
                    val type = object : TypeToken<List<Course>>() {}.type
                    allCourses = gson.fromJson(importedJson, type)
                } catch (_: Exception) {
                    prefs.edit().remove(KEY_IMPORTED_COURSES).apply()
                    allCourses = loadDefaultCourses()
                }
            } else {
                allCourses = loadDefaultCourses()
            }
        }
        return allCourses
    }

    private fun loadDefaultCourses(): List<Course> {
        val inputStream = context.resources.openRawResource(
            context.resources.getIdentifier(BuildConfig.COURSE_FILE, "raw", context.packageName)
        )
        val json = inputStream.bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Course>>() {}.type
        return gson.fromJson(json, type)
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
        require(courses.isNotEmpty()) { "课表数据为空" }
        for (c in courses) {
            require(c.name.isNotBlank()) { "存在课程名称为空" }
            require(c.day in 1..7) { "星期值无效：${c.day}" }
            require(c.start >= 1) { "起始节数无效：${c.start}" }
            require(c.end >= c.start) { "结束节数小于起始节数" }
            require(c.weeks.isNotBlank()) { "存在周数信息为空" }
        }
        // 先清除旧数据，再写入新数据，防止缓存导致重复
        allCourses = emptyList()
        prefs.edit().remove(KEY_IMPORTED_COURSES).apply()
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

    fun getSemesterStartOrDefault(): Long {
        return getSemesterStart() ?: run {
            val cal = java.util.Calendar.getInstance()
            cal.set(2026, 2, 2, 0, 0, 0) // 默认2026年3月2日
            cal.set(java.util.Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }
    }

    fun setSemesterStart(dateMillis: Long) {
        prefs.edit().putLong(KEY_SEMESTER_START, dateMillis).apply()
    }

    fun loadGradePointTable(): Map<String, List<GradePointRange>> {
        if (gradePointTable == null) {
            try {
                val inputStream = context.resources.openRawResource(
                    context.resources.getIdentifier("grade_point", "raw", context.packageName)
                )
                val json = inputStream.bufferedReader().use { it.readText() }
                val type = object : TypeToken<Map<String, GradePointConfig>>() {}.type
                val config: Map<String, GradePointConfig> = gson.fromJson(json, type)
                gradePointTable = config.mapValues { it.value.ranges }
            } catch (_: Exception) {
                gradePointTable = emptyMap()
            }
        }
        return gradePointTable!!
    }

    fun getGradePointRanges(): List<GradePointRange> {
        val table = loadGradePointTable()
        return table[BuildConfig.SCHOOL_ID] ?: table["swu"] ?: emptyList()
    }

    fun calculateGradePoint(score: Int): Float {
        val ranges = getGradePointRanges()
        for (range in ranges) {
            if (score >= range.min && score <= range.max) {
                return range.gradePoint
            }
        }
        return 0.0f
    }

    fun getGradeLevel(score: Int): String {
        val ranges = getGradePointRanges()
        for (range in ranges) {
            if (score >= range.min && score <= range.max) {
                return range.level
            }
        }
        return "不及格"
    }

    fun loadGrades(): List<Grade> {
        val json = prefs.getString(KEY_GRADES, null) ?: return emptyList()
        val type = object : TypeToken<List<Grade>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveGrade(grade: Grade) {
        val grades = loadGrades().toMutableList()
        grades.add(grade)
        prefs.edit().putString(KEY_GRADES, gson.toJson(grades)).apply()
    }

    fun deleteGrade(id: Long) {
        val grades = loadGrades().filter { it.id != id }
        prefs.edit().putString(KEY_GRADES, gson.toJson(grades)).apply()
    }

    companion object {
        private const val KEY_IMPORTED_COURSES = "imported_courses"
        private const val KEY_EXAMS = "exams"
        private const val KEY_SEMESTER_START = "semester_start"
        private const val KEY_GRADES = "grades"
    }
}
