package com.example.coursetable.data

data class GradePointConfig(
    val name: String,
    val scale: String,
    val ranges: List<GradePointRange>
)

data class GradePointRange(
    val min: Int,
    val max: Int,
    val gradePoint: Float,
    val level: String
)
