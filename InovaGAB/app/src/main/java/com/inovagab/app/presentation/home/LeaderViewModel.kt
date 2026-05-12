package com.inovagab.app.presentation.home

import androidx.lifecycle.ViewModel
import com.inovagab.app.data.repository.AppRepository

class LeaderViewModel(private val repository: AppRepository) : ViewModel() {
    val currentUser = repository.currentUser
    val guidelines = repository.guidelines
    val projects = repository.projects

    fun logout() {
        repository.logout()
    }

    fun addGuideline(titulo: String, desc: String, cat: String) {
        repository.addGuideline(titulo, desc, cat)
    }
}
