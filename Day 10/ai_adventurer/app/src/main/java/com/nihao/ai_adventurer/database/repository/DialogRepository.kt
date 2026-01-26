package com.nihao.ai_adventurer.database.repository

import com.nihao.ai_adventurer.data.Message
import com.nihao.ai_adventurer.database.dao.DialogDao
import com.nihao.ai_adventurer.database.dao.DialogMessageDao
import com.nihao.ai_adventurer.database.entities.Dialog
import com.nihao.ai_adventurer.database.entities.DialogMessage
import kotlinx.coroutines.flow.Flow

class DialogRepository(
    private val dialogDao: DialogDao,
    private val dialogMessageDao: DialogMessageDao
) {
    fun getAllDialogs(): Flow<List<Dialog>> {
        return dialogDao.getAllDialogs()
    }
    
    suspend fun getDialogById(dialogId: Long): Dialog? {
        return dialogDao.getDialogById(dialogId)
    }
    
    suspend fun createDialog(title: String, promptId: String, modelId: String): Long {
        val dialog = Dialog(
            title = title,
            promptId = promptId,
            modelId = modelId
        )
        return dialogDao.insertDialog(dialog)
    }
    
    suspend fun updateDialog(dialog: Dialog) {
        dialogDao.updateDialog(dialog)
    }
    
    suspend fun deleteDialog(dialog: Dialog) {
        dialogDao.deleteDialog(dialog)
    }
    
    suspend fun updateDialogTimestamp(dialogId: Long) {
        dialogDao.updateDialogTimestamp(dialogId, System.currentTimeMillis())
    }
    
    suspend fun updateDialogTokens(dialogId: Long, tokens: Int) {
        dialogDao.updateDialogTokens(dialogId, tokens)
    }
    
    // Message operations
    fun getMessagesByDialogId(dialogId: Long): Flow<List<DialogMessage>> {
        return dialogMessageDao.getMessagesByDialogId(dialogId)
    }
    
    suspend fun getMessagesByDialogIdOnce(dialogId: Long): List<DialogMessage> {
        return dialogMessageDao.getMessagesByDialogIdOnce(dialogId)
    }
    
    suspend fun getActiveMessagesByDialogId(dialogId: Long): List<DialogMessage> {
        return dialogMessageDao.getActiveMessagesByDialogId(dialogId)
    }
    
    suspend fun insertMessage(dialogId: Long, message: Message) {
        val dialogMessage = DialogMessage(
            dialogId = dialogId,
            messageId = message.id,
            text = message.text,
            isFromUser = message.isFromUser,
            timestamp = message.timestamp,
            title = message.title,
            isError = message.isError,
            tags = message.tags.joinToString(","),
            urls = message.urls.joinToString(","),
            responseTimeMs = message.responseTimeMs,
            isActive = true
        )
        dialogMessageDao.insertMessage(dialogMessage)
        updateDialogTimestamp(dialogId)
    }
    
    suspend fun markAllMessagesAsInactive(dialogId: Long) {
        dialogMessageDao.markAllMessagesAsInactive(dialogId)
    }
    
    suspend fun updateMessageActiveStatus(messageId: Long, isActive: Boolean) {
        dialogMessageDao.updateMessageActiveStatus(messageId, isActive)
    }
}

/**
 * Converts DialogMessage to Message for UI
 */
fun com.nihao.ai_adventurer.database.entities.DialogMessage.toMessage(): Message {
    return Message(
        id = messageId,
        text = text,
        isFromUser = isFromUser,
        timestamp = timestamp,
        title = title,
        isError = isError,
        tags = if (tags.isNotBlank()) tags.split(",") else emptyList(),
        urls = if (urls.isNotBlank()) urls.split(",") else emptyList(),
        responseTimeMs = responseTimeMs,
        isActive = isActive,
        dbId = id
    )
}
