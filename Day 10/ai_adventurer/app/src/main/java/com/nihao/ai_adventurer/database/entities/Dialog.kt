package com.nihao.ai_adventurer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dialogs")
data class Dialog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val promptId: String = "questions_response",
    val modelId: String = "deepseek-chat",
    val totalTokens: Int = 0
)
