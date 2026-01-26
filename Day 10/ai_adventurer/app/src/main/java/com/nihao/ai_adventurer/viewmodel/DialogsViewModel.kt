package com.nihao.ai_adventurer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nihao.ai_adventurer.database.AppDatabase
import com.nihao.ai_adventurer.database.entities.Dialog
import com.nihao.ai_adventurer.database.repository.DialogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DialogsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = DialogRepository(
        dialogDao = database.dialogDao(),
        dialogMessageDao = database.dialogMessageDao()
    )
    
    private val _dialogs = MutableStateFlow<List<Dialog>>(emptyList())
    val dialogs: StateFlow<List<Dialog>> = _dialogs.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadDialogs()
    }
    
    private fun loadDialogs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllDialogs().collect { dialogList ->
                    _dialogs.value = dialogList
                    _isLoading.value = false // Отключаем индикатор после первой загрузки
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при загрузке диалогов: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun deleteDialog(dialog: Dialog) {
        viewModelScope.launch {
            try {
                repository.deleteDialog(dialog)
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при удалении диалога: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
