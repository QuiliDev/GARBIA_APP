package com.garbia.app.data.model

data class RankingEntry(
    val position: Int,
    val name: String,
    val initials: String,
    val points: Int,
    val level: String,
    val isCurrentUser: Boolean = false,
    val firebaseUid: String? = null
)
