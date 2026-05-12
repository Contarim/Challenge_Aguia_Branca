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

    fun approveIdeaAndCreateProject(
        ideaId: String, 
        prioridade: Priority,
        risco: Risk,
        area: String,
        dataInicio: String,
        dataFim: String,
        investimento: Double,
        retorno: Double,
        titulo: String,
        descricao: String
    ) {
        // Primeiro, aprova a ideia
        repository.updateIdeaStatus(ideaId, IdeaStatus.APROVADA, prioridade)
        
        // Em seguida, cria o projeto de forma automatizada com os dados do modal
        repository.addProject(
            titulo = titulo,
            descricao = descricao,
            investimento = investimento,
            prioridade = prioridade,
            risco = risco,
            area = area,
            dataInicio = dataInicio,
            dataFim = dataFim,
            retorno = retorno
        )
    }
}
