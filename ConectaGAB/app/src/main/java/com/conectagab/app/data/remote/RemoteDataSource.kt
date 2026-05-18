package com.conectagab.app.data.remote

import com.conectagab.app.data.model.*

/**
 * Camada de acesso remoto. Todas as chamadas são suspend fun — executadas em
 * coroutines pelos ViewModels / Repository. Em caso de falha, lança exceção para
 * que o chamador decida usar o fallback local (MockData).
 */
class RemoteDataSource(private val api: ApiService = RetrofitClient.apiService) {

    // ── Users ─────────────────────────────────────────────────────────────────
    suspend fun getUsers(): List<User> = api.getUsers()

    // ── Guidelines ────────────────────────────────────────────────────────────
    suspend fun getGuidelines(): List<StrategicGuideline> = api.getGuidelines()

    suspend fun createGuideline(guideline: StrategicGuideline): StrategicGuideline =
        api.createGuideline(guideline)

    suspend fun updateGuideline(guideline: StrategicGuideline): StrategicGuideline =
        api.updateGuideline(guideline.id, guideline)

    suspend fun deleteGuideline(id: String) {
        api.deleteGuideline(id)
    }

    // ── Ideas ─────────────────────────────────────────────────────────────────
    suspend fun getIdeas(): List<Idea> = api.getIdeas()

    suspend fun createIdea(idea: Idea): Idea = api.createIdea(idea)

    suspend fun updateIdea(idea: Idea): Idea = api.updateIdea(idea.id, idea)

    // ── Projects ──────────────────────────────────────────────────────────────
    suspend fun getProjects(): List<Project> = api.getProjects()

    suspend fun createProject(project: Project): Project = api.createProject(project)

    suspend fun updateProject(project: Project): Project = api.updateProject(project.id, project)
}
