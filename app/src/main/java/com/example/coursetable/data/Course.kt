package com.example.coursetable.data

import com.google.gson.annotations.SerializedName

data class Course(
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
