package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String = "Action",
    val iconEmoji: String = "🎮",
    val boostCount: Int = 0,
    val lastBoosted: Long = 0L,
    val packageName: String? = null
)
