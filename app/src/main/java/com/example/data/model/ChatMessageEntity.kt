package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val senderId: String,
    val senderName: String,
    val isFromOwner: Boolean,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis()
)
