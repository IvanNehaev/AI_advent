package com.nihao.ai_adventurer.database.dao

import androidx.room.*
import com.nihao.ai_adventurer.database.entities.DialogMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface DialogMessageDao {
    @Query("SELECT * FROM dialog_messages WHERE dialogId = :dialogId ORDER BY timestamp ASC")
    fun getMessagesByDialogId(dialogId: Long): Flow<List<DialogMessage>>
    
    @Query("SELECT * FROM dialog_messages WHERE dialogId = :dialogId ORDER BY timestamp ASC")
    suspend fun getMessagesByDialogIdOnce(dialogId: Long): List<DialogMessage>
    
    @Query("SELECT * FROM dialog_messages WHERE dialogId = :dialogId AND isActive = 1 ORDER BY timestamp ASC")
    suspend fun getActiveMessagesByDialogId(dialogId: Long): List<DialogMessage>
    
    @Insert
    suspend fun insertMessage(message: DialogMessage): Long
    
    @Update
    suspend fun updateMessage(message: DialogMessage)
    
    @Delete
    suspend fun deleteMessage(message: DialogMessage)
    
    @Query("UPDATE dialog_messages SET isActive = 0 WHERE dialogId = :dialogId AND isActive = 1")
    suspend fun markAllMessagesAsInactive(dialogId: Long)
    
    @Query("UPDATE dialog_messages SET isActive = :isActive WHERE id = :messageId")
    suspend fun updateMessageActiveStatus(messageId: Long, isActive: Boolean)
    
    @Query("DELETE FROM dialog_messages WHERE dialogId = :dialogId")
    suspend fun deleteMessagesByDialogId(dialogId: Long)
}
