package com.conectagab.app.presentation.home

import androidx.lifecycle.ViewModel
import com.conectagab.app.data.model.*
import com.conectagab.app.data.repository.AppRepository

class ManagerViewModel(private val repository: AppRepository) : ViewModel() {
    val currentUser = repository.currentUser
    val ideas = repository.ideas
    val projects = repository.projects
    val guidelines = repository.guidelines

    fun logout() {
        repository.logout()
    }

    // ── Ideas ─────────────────────────────────────────────────────────────────
    fun prioritizeIdea(ideaId: String) {
        repository.updateIdeaStatus(ideaId, IdeaStatus.PRIORIZADA)
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
        repository.updateIdeaStatus(ideaId, IdeaStatus.APROVADA, prioridade)
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

    // ── Projects ──────────────────────────────────────────────────────────────
    fun updateProject(
        projectId: String,
        responsavelNome: String,
        area: String,
        dataInicio: String,
        dataFim: String,
        investimento: Double,
        retorno: Double,
        status: ProjectStatus,
        progresso: Int? = null,
        etapa: String? = null,
        economiaReal: Double? = null,
        ganhoProdutividade: Double? = null
    ) {
        repository.updateProject(
            projectId, responsavelNome, area, dataInicio, dataFim,
            investimento, retorno, status, progresso, etapa, economiaReal, ganhoProdutividade
        )
    }
}
