package com.example.coursetable.data

data class Course(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val day: Int,
    val start: Int,
    val end: Int,
    val room: String = "",
    val teacher: String = "",
    val weeks: String,
    val type: String = "讲课",
    val color: String = "#4F46E5"
)
