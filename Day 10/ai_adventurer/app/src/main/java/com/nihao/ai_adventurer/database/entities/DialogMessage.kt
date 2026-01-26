package com.nihao.ai_adventurer.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dialog_messages",
    foreignKeys = [
        ForeignKey(
            entity = Dialog::class,
            parentColumns = ["id"],
            childColumns = ["dialogId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["dialogId"])]
)
data class DialogMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dialogId: Long,
    val messageId: String, // UUID from Message class
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String? = null,
    val isError: Boolean = false,
    val tags: String = "", // Comma-separated tags
    val urls: String = "", // Comma-separated URLs
    val responseTimeMs: Long? = null,
    val isActive: Boolean = true // TRUE when message has not been summarized
)
