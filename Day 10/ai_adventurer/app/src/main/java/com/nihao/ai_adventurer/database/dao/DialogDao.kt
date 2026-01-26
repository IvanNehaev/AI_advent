package com.nihao.ai_adventurer.database.dao

import androidx.room.*
import com.nihao.ai_adventurer.database.entities.Dialog
import kotlinx.coroutines.flow.Flow

@Dao
interface DialogDao {
    @Query("SELECT * FROM dialogs ORDER BY updatedAt DESC")
    fun getAllDialogs(): Flow<List<Dialog>>
    
    @Query("SELECT * FROM dialogs WHERE id = :dialogId")
    suspend fun getDialogById(dialogId: Long): Dialog?
    
    @Insert
    suspend fun insertDialog(dialog: Dialog): Long
    
    @Update
    suspend fun updateDialog(dialog: Dialog)
    
    @Delete
    suspend fun deleteDialog(dialog: Dialog)
    
    @Query("UPDATE dialogs SET updatedAt = :timestamp WHERE id = :dialogId")
    suspend fun updateDialogTimestamp(dialogId: Long, timestamp: Long)
    
    @Query("UPDATE dialogs SET totalTokens = :tokens WHERE id = :dialogId")
    suspend fun updateDialogTokens(dialogId: Long, tokens: Int)
}
