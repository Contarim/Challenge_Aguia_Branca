package com.inovagab.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inovagab.app.data.model.Idea
import com.inovagab.app.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class OperatorViewModel(private val repository: AppRepository) : ViewModel() {
    val currentUser = repository.currentUser
    val guidelines = repository.guidelines
    
    val myIdeas: StateFlow<List<Idea>> = repository.ideas.map { ideas -> 
        ideas.filter { it.autorId == repository.currentUser.value?.id } 
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _ideaTitle = MutableStateFlow("")
    val ideaTitle = _ideaTitle.asStateFlow()

    private val _ideaDesc = MutableStateFlow("")
    val ideaDesc = _ideaDesc.asStateFlow()

    private val _ideaCat = MutableStateFlow("")
    val ideaCat = _ideaCat.asStateFlow()

    fun onTitleChange(v: String) { _ideaTitle.value = v }
    fun onDescChange(v: String) { _ideaDesc.value = v }
    fun onCatChange(v: String) { _ideaCat.value = v }

    fun submitIdea(onSuccess: () -> Unit) {
        if (_ideaTitle.value.isNotBlank() && _ideaDesc.value.isNotBlank()) {
            repository.addIdea(_ideaTitle.value, _ideaDesc.value, _ideaCat.value)
            _ideaTitle.value = ""
            _ideaDesc.value = ""
            _ideaCat.value = ""
            onSuccess()
        }
    }

    fun logout() {
        repository.logout()
    }
}
