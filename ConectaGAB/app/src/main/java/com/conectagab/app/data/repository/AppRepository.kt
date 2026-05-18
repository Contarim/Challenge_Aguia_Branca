package com.conectagab.app.data.repository

import com.conectagab.app.data.local.SessionDataStore
import com.conectagab.app.data.mock.MockData
import com.conectagab.app.data.model.*
import com.conectagab.app.data.remote.RemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AppRepository(
    private val remote: RemoteDataSource = RemoteDataSource(),
    private val sessionDataStore: SessionDataStore? = null
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _guidelines = MutableStateFlow<List<StrategicGuideline>>(MockData.guidelines)
    val guidelines: StateFlow<List<StrategicGuideline>> = _guidelines.asStateFlow()

    private val _ideas = MutableStateFlow<List<Idea>>(MockData.ideas)
    val ideas: StateFlow<List<Idea>> = _ideas.asStateFlow()

    private val _projects = MutableStateFlow<List<Project>>(MockData.projects)
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ── Bootstrap: carrega dados da API na inicialização ─────────────────────
    init {
        loadRemoteData()
        initSessionRestore()
    }

    private fun initSessionRestore() {
        sessionDataStore?.let { store ->
            scope.launch {
                store.savedUserEmail.collect { email ->
                    if (email != null && _currentUser.value == null) {
                        val user = MockData.users.find { it.email == email }
                        if (user != null) {
                            _currentUser.value = user
                        }
                    }
                }
            }
        }
    }

    private fun loadRemoteData() {
        scope.launch {
            _isLoading.value = true
            try {
                val remoteGuidelines = remote.getGuidelines()
                if (remoteGuidelines.isNotEmpty()) _guidelines.value = remoteGuidelines
            } catch (_: Exception) { /* Mantém MockData como fallback */ }

            try {
                val remoteIdeas = remote.getIdeas()
                if (remoteIdeas.isNotEmpty()) _ideas.value = remoteIdeas
            } catch (_: Exception) { /* Fallback */ }

            try {
                val remoteProjects = remote.getProjects()
                if (remoteProjects.isNotEmpty()) _projects.value = remoteProjects
            } catch (_: Exception) { /* Fallback */ }

            _isLoading.value = false
        }
    }

    // ── Auth ──────────────────────────────────────────────────────────────────
    fun login(email: String, senha: String): Result<User> {
        // Primeiro tenta na lista local (inclui dados da API carregados)
        val user = MockData.users.find { it.email == email && it.senha == senha }
        return if (user != null) {
            _currentUser.value = user
            scope.launch {
                sessionDataStore?.saveSession(user.id, user.email)
            }
            Result.success(user)
        } else {
            Result.failure(Exception("Credenciais inválidas"))
        }
    }

    fun logout() {
        _currentUser.value = null
        scope.launch {
            sessionDataStore?.clearSession()
        }
    }

    // ── Guidelines ────────────────────────────────────────────────────────────
    fun addGuideline(titulo: String, descricao: String, categoria: String) {
        val newGuideline = StrategicGuideline(
            id = UUID.randomUUID().toString(),
            titulo = titulo,
            descricao = descricao,
            categoria = categoria
        )
        _guidelines.value = _guidelines.value + newGuideline
        scope.launch {
            try { remote.createGuideline(newGuideline) } catch (_: Exception) {}
        }
    }

    fun updateGuideline(id: String, titulo: String, descricao: String, categoria: String) {
        val currentList = _guidelines.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val updated = currentList[index].copy(titulo = titulo, descricao = descricao, categoria = categoria)
            currentList[index] = updated
            _guidelines.value = currentList
            scope.launch {
                try { remote.updateGuideline(updated) } catch (_: Exception) {}
            }
        }
    }

    fun deleteGuideline(id: String) {
        _guidelines.value = _guidelines.value.filter { it.id != id }
        scope.launch {
            try { remote.deleteGuideline(id) } catch (_: Exception) {}
        }
    }

    // ── Ideas ─────────────────────────────────────────────────────────────────
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
        _ideas.value = _ideas.value + newIdea
        scope.launch {
            try { remote.createIdea(newIdea) } catch (_: Exception) {}
        }
    }

    fun updateIdeaStatus(ideaId: String, status: IdeaStatus, prioridade: Priority? = null) {
        val currentList = _ideas.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == ideaId }
        if (index != -1) {
            val updated = currentList[index].copy(
                status = status,
                prioridade = prioridade ?: currentList[index].prioridade
            )
            currentList[index] = updated
            _ideas.value = currentList
            scope.launch {
                try { remote.updateIdea(updated) } catch (_: Exception) {}
            }
        }
    }

    // ── Projects ──────────────────────────────────────────────────────────────
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
        _projects.value = _projects.value + newProject
        scope.launch {
            try { remote.createProject(newProject) } catch (_: Exception) {}
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
        status: ProjectStatus,
        progresso: Int? = null,
        etapa: String? = null,
        economiaReal: Double? = null,
        ganhoProdutividade: Double? = null
    ) {
        val currentList = _projects.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == projectId }
        if (index != -1) {
            val updated = currentList[index].copy(
                responsavelNome = responsavelNome,
                areaResponsavel = area,
                dataInicioPrevista = dataInicio,
                dataFimPrevista = dataFim,
                investimentoEstimado = investimento,
                retornoEstimado = retorno,
                status = status,
                categoria = area,
                progressoPercentual = progresso ?: currentList[index].progressoPercentual,
                etapaAtual = etapa ?: currentList[index].etapaAtual,
                economiaEstimada = economiaReal ?: currentList[index].economiaEstimada,
                ganhoProdutividade = ganhoProdutividade ?: currentList[index].ganhoProdutividade
            )
            currentList[index] = updated
            _projects.value = currentList
            scope.launch {
                try { remote.updateProject(updated) } catch (_: Exception) {}
            }
        }
    }
}
