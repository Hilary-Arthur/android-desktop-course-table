package com.example.coursetable.data

data class Exam(
    val id: Long,
    val subject: String,
    val examTime: String,
    val room: String = ""
)
