package com.example.coursetable.util

object WeekParser {
    fun parse(weeksStr: String): List<Int> {
        val result = mutableListOf<Int>()
        val parts = weeksStr.split(",")

        for (part in parts) {
            val trimmed = part.trim()
            when {
                trimmed.contains("(单)") -> {
                    val range = trimmed.replace("(单)", "").trim()
                    val (start, end) = parseRange(range)
                    for (i in start..end) {
                        if (i % 2 == 1) result.add(i)
                    }
                }
                trimmed.contains("(双)") -> {
                    val range = trimmed.replace("(双)", "").trim()
                    val (start, end) = parseRange(range)
                    for (i in start..end) {
                        if (i % 2 == 0) result.add(i)
                    }
                }
                trimmed.contains("-") -> {
                    val (start, end) = parseRange(trimmed)
                    for (i in start..end) {
                        result.add(i)
                    }
                }
                else -> {
                    trimmed.toIntOrNull()?.let { result.add(it) }
                }
            }
        }
        return result.distinct().sorted()
    }

    private fun parseRange(range: String): Pair<Int, Int> {
        val parts = range.split("-")
        return if (parts.size == 2) {
            Pair(parts[0].trim().toInt(), parts[1].trim().toInt())
        } else {
            val num = parts[0].trim().toInt()
            Pair(num, num)
        }
    }

    fun isWeekActive(weeksStr: String, week: Int): Boolean {
        return parse(weeksStr).contains(week)
    }

    fun getMaxWeek(courses: List<com.example.coursetable.data.Course>): Int {
        var maxWeek = 1
        for (course in courses) {
            val weeks = parse(course.weeks)
            if (weeks.isNotEmpty()) {
                maxWeek = maxOf(maxWeek, weeks.max())
            }
        }
        return maxWeek
    }
}
