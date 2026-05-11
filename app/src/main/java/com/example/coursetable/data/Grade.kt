package com.example.coursetable.data

data class Grade(
    val id: Long = System.currentTimeMillis(),
    val courseName: String,
    val score: Int,           // 百分制成绩
    val credit: Float,        // 学分
    val gradePoint: Float,    // 绩点（自动计算）
    val semester: String = "" // 学期标识，如 "2024-2025-1"
) {
    companion object {
        // 根据百分制成绩计算绩点（四舍五入）
        fun calculateGradePoint(score: Int): Float {
            return when {
                score >= 90 -> 4.0f
                score >= 85 -> 3.7f
                score >= 82 -> 3.3f
                score >= 78 -> 3.0f
                score >= 75 -> 2.7f
                score >= 72 -> 2.3f
                score >= 68 -> 2.0f
                score >= 64 -> 1.5f
                score >= 60 -> 1.0f
                else -> 0.0f
            }
        }

        // 计算平均绩点（加权平均）
        fun calculateGPA(grades: List<Grade>): Float {
            if (grades.isEmpty()) return 0.0f
            val totalCredits = grades.sumOf { it.credit.toDouble() }.toFloat()
            if (totalCredits == 0.0f) return 0.0f
            val weightedSum = grades.sumOf { (it.gradePoint * it.credit).toDouble() }.toFloat()
            return (weightedSum / totalCredits * 100).toInt() / 100f // 保留两位小数
        }
    }
}
