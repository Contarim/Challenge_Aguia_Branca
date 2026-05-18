package com.conectagab.app.presentation.home

import androidx.lifecycle.ViewModel
import com.conectagab.app.data.model.ProjectStatus
import com.conectagab.app.data.repository.AppRepository

class LeaderViewModel(private val repository: AppRepository) : ViewModel() {
    val currentUser = repository.currentUser
    val guidelines = repository.guidelines
    val projects = repository.projects

    fun logout() {
        repository.logout()
    }

    // ── Guidelines CRUD ───────────────────────────────────────────────────────
    fun addGuideline(titulo: String, desc: String, cat: String) {
        repository.addGuideline(titulo, desc, cat)
    }

    fun updateGuideline(id: String, titulo: String, desc: String, cat: String) {
        repository.updateGuideline(id, titulo, desc, cat)
    }

    fun deleteGuideline(id: String) {
        repository.deleteGuideline(id)
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
