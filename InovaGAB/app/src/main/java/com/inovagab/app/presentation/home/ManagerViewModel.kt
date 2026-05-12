package com.inovagab.app.presentation.home

import androidx.lifecycle.ViewModel
import com.inovagab.app.data.model.*
import com.inovagab.app.data.repository.AppRepository

class ManagerViewModel(private val repository: AppRepository) : ViewModel() {
    val currentUser = repository.currentUser
    val ideas = repository.ideas
    val projects = repository.projects

    fun logout() {
        repository.logout()
    }

    fun approveIdea(ideaId: String, prioridade: Priority) {
        repository.updateIdeaStatus(ideaId, IdeaStatus.APROVADA, prioridade)
    }

    fun createProject(titulo: String, desc: String, invest: Double) {
        repository.addProject(titulo = titulo, descricao = desc, investimento = invest)
    }
}
