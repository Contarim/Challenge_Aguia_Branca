package com.conectagab.app.data.repository

import com.conectagab.app.data.mock.MockData
import com.conectagab.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AppRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _guidelines = MutableStateFlow<List<StrategicGuideline>>(MockData.guidelines)
    val guidelines: StateFlow<List<StrategicGuideline>> = _guidelines.asStateFlow()

    private val _ideas = MutableStateFlow<List<Idea>>(MockData.ideas)
    val ideas: StateFlow<List<Idea>> = _ideas.asStateFlow()

    private val _projects = MutableStateFlow<List<Project>>(MockData.projects)
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    fun login(email: String, senha: String): Result<User> {
        val user = MockData.users.find { it.email == email && it.senha == senha }
        return if (user != null) {
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Credenciais inválidas"))
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun addIdea(titulo: String, descricao: String, categoria: String) {
        val user = _currentUser.value ?: return
        val newIdea = Idea(
            id = UUID.randomUUID().toString(),
            titulo = titulo,
            descricao = descricao,
            categoria = categoria,
            autorId = user.id,
            autorNome = user.nome,
            tags = listOf(categoria)
        )
        val currentList = _ideas.value.toMutableList()
        currentList.add(newIdea)
        _ideas.value = currentList
    }

    fun updateIdeaStatus(ideaId: String, status: IdeaStatus, prioridade: Priority? = null) {
        val currentList = _ideas.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == ideaId }
        if (index != -1) {
            val idea = currentList[index]
            currentList[index] = idea.copy(status = status, prioridade = prioridade ?: idea.prioridade)
            _ideas.value = currentList
        }
    }

    fun addGuideline(titulo: String, descricao: String, categoria: String) {
        val newGuideline = StrategicGuideline(
            id = UUID.randomUUID().toString(),
            titulo = titulo,
            descricao = descricao,
            categoria = categoria
        )
        val currentList = _guidelines.value.toMutableList()
        currentList.add(newGuideline)
        _guidelines.value = currentList
    }

    fun addProject(
        titulo: String, descricao: String, investimento: Double,
        prioridade: Priority, risco: Risk, area: String,
        dataInicio: String, dataFim: String, retorno: Double
    ) {
        val user = _currentUser.value ?: return
        val newProject = Project(
            id = UUID.randomUUID().toString(),
            titulo = titulo,
            descricao = descricao,
            responsavelId = user.id,
            responsavelNome = user.nome,
            areaResponsavel = area,
            etapaAtual = "Planejamento",
            status = ProjectStatus.EM_ANDAMENTO,
            prioridade = prioridade,
            risco = risco,
            dataInicioPrevista = dataInicio,
            dataFimPrevista = dataFim,
            investimentoEstimado = investimento,
            retornoEstimado = retorno,
            economiaEstimada = 0.0,
            ganhoProdutividade = 0.0,
            progressoPercentual = 0,
            categoria = area,
            tags = listOf(area, prioridade.label.split(" ")[0]),
            comparativoYOY = 0.0
        )
        val currentList = _projects.value.toMutableList()
        currentList.add(newProject)
        _projects.value = currentList
    }

    fun updateProjectProgress(projectId: String, progresso: Int, status: ProjectStatus, etapa: String) {
        val currentList = _projects.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == projectId }
        if (index != -1) {
            val project = currentList[index]
            currentList[index] = project.copy(progressoPercentual = progresso, status = status, etapaAtual = etapa)
            _projects.value = currentList
        }
    }

    fun updateProject(
        projectId: String,
        responsavelNome: String,
        area: String,
        dataInicio: String,
        dataFim: String,
        investimento: Double,
        retorno: Double,
        status: ProjectStatus
    ) {
        val currentList = _projects.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == projectId }
        if (index != -1) {
            val project = currentList[index]
            currentList[index] = project.copy(
                responsavelNome = responsavelNome,
                areaResponsavel = area,
                dataInicioPrevista = dataInicio,
                dataFimPrevista = dataFim,
                investimentoEstimado = investimento,
                retornoEstimado = retorno,
                status = status,
                categoria = area
            )
            _projects.value = currentList
        }
    }
}
