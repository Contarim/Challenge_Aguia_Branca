package com.conectagab.app.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.conectagab.app.utils.DateVisualTransformation
import com.conectagab.app.utils.FormatUtils
import androidx.lifecycle.viewmodel.compose.viewModel
import com.conectagab.app.data.model.*
import com.conectagab.app.presentation.AppViewModelFactory
import com.conectagab.app.ui.components.*
import com.conectagab.app.ui.theme.*
import com.conectagab.app.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerHomeScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: ManagerViewModel = viewModel(factory = AppViewModelFactory)
) {
    val user by viewModel.currentUser.collectAsState(initial = null)
    val ideas by viewModel.ideas.collectAsState(initial = emptyList())
    val projects by viewModel.projects.collectAsState(initial = emptyList())
    val guidelines by viewModel.guidelines.collectAsState(initial = emptyList())

    var selectedTab by remember { mutableStateOf(0) }
    var ideaToApprove by remember { mutableStateOf<Idea?>(null) }
    var projectToEdit by remember { mutableStateOf<Project?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(GabDarkBlue), contentAlignment = Alignment.Center) {
                            Text(user?.nome?.take(1) ?: "M", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Curadoria de Inovação", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GabTextPrimary)
                            Text("Avaliador Tático", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.logout(); onNavigateToLogin() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Sair", tint = GabTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GabSurface)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = GabSurface, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.FilterList, contentDescription = null) },
                    label = { Text("Funil") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = StatusInfoBg, selectedIconColor = GabAccent, selectedTextColor = GabAccent)
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.AccountTree, contentDescription = null) },
                    label = { Text("Projetos") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = StatusInfoBg, selectedIconColor = GabAccent, selectedTextColor = GabAccent)
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Map, contentDescription = null) },
                    label = { Text("Diretrizes") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = StatusInfoBg, selectedIconColor = GabAccent, selectedTextColor = GabAccent)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(GabBackground)) {
            when (selectedTab) {
                0 -> ManagerIdeasFunnel(
                    ideas = ideas,
                    onPrioritizeClick = { viewModel.prioritizeIdea(it.id) },
                    onApproveClick = { ideaToApprove = it }
                )
                1 -> ManagerProjectsContent(projects, onProjectClick = { projectToEdit = it })
                2 -> ManagerGuidelinesContent(guidelines)
            }
        }

        // Dialog de aprovação
        ideaToApprove?.let { idea ->
            ApproveAndCreateProjectDialog(
                onDismiss = { ideaToApprove = null },
                onConfirm = { prioridade, risco, area, dataInicio, dataFim, invest, retorno ->
                    viewModel.approveIdeaAndCreateProject(
                        idea.id, prioridade, risco, area, dataInicio, dataFim, invest, retorno, idea.titulo, idea.descricao
                    )
                    ideaToApprove = null
                }
            )
        }

        // Dialog de edição de projeto
        projectToEdit?.let { proj ->
            SharedEditProjectDialog(
                project = proj,
                onDismiss = { projectToEdit = null },
                onSave = { pId, resp, area, inicio, fim, inv, ret, status, progresso, etapa, economia, produtividade ->
                    viewModel.updateProject(pId, resp, area, inicio, fim, inv, ret, status, progresso, etapa, economia, produtividade)
                    projectToEdit = null
                }
            )
        }
    }
}

// ── Funil de Ideias ───────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerIdeasFunnel(
    ideas: List<Idea>,
    onPrioritizeClick: (Idea) -> Unit,
    onApproveClick: (Idea) -> Unit
) {
    val pendingIdeas = ideas.filter { it.status == IdeaStatus.CADASTRADA || it.status == IdeaStatus.EM_ANALISE || it.status == IdeaStatus.PRIORIZADA }
    var selectedCategory by remember { mutableStateOf<String?>("Todas") }
    val categories = listOf("Todas") + pendingIdeas.flatMap { it.tags }.distinct()
    val filteredIdeas = if (selectedCategory == "Todas") pendingIdeas else pendingIdeas.filter { it.tags.contains(selectedCategory) }

    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Text("Funil de Triagem", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = cat == selectedCategory,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GabDarkBlue, selectedLabelColor = Color.White)
                    )
                }
            }
        }

        if (filteredIdeas.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    EmptyState("Nenhuma ideia pendente", "Todas as ideias foram triadas ou não há envios.", Icons.Default.Inbox)
                }
            }
        } else {
            items(filteredIdeas) { idea ->
                IdeaFunnelCard(
                    idea = idea,
                    onPrioritize = { onPrioritizeClick(idea) },
                    onApprove = { onApproveClick(idea) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun IdeaFunnelCard(
    idea: Idea,
    onPrioritize: () -> Unit,
    onApprove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GabSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(idea.titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GabTextPrimary)
                    Text(idea.categoria, style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(
                        when (idea.status) {
                            IdeaStatus.PRIORIZADA -> StatusWarningBg
                            else -> GabSecondary
                        }
                    ).padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(idea.status.label, style = MaterialTheme.typography.labelSmall,
                        color = if (idea.status == IdeaStatus.PRIORIZADA) StatusWarning else GabTextPrimary,
                        fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(idea.descricao, style = MaterialTheme.typography.bodySmall, color = GabTextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = GabTextSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(idea.autorNome, style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                // Botão Priorizar — só aparece se não estiver já priorizada
                if (idea.status != IdeaStatus.PRIORIZADA) {
                    OutlinedButton(
                        onClick = onPrioritize,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, StatusWarning)
                    ) {
                        Icon(Icons.Default.Flag, contentDescription = null, tint = StatusWarning, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Priorizar", color = StatusWarning, fontSize = 12.sp)
                    }
                }
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Aprovar", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

// ── Projetos ──────────────────────────────────────────────────────────────────
@Composable
fun ManagerProjectsContent(projects: List<Project>, onProjectClick: (Project) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Text("Gestão de Execução", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Text("Toque em um projeto para atualizar dados e resultados", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (projects.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    EmptyState("Nenhum projeto", "Projetos aparecem aqui após aprovação de ideias.", Icons.Default.AccountTree)
                }
            }
        } else {
            items(projects.sortedBy { it.statusPrazo.ordinal }) { project ->
                ProjectCard(project = project, onClick = { onProjectClick(project) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// ── Diretrizes (leitura) ──────────────────────────────────────────────────────
@Composable
fun ManagerGuidelinesContent(guidelines: List<com.conectagab.app.data.model.StrategicGuideline>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Text("Diretrizes Estratégicas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Text("Orientações definidas pela liderança", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
            Spacer(modifier = Modifier.height(24.dp))
        }
        if (guidelines.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    EmptyState("Nenhuma Diretriz", "A liderança ainda não cadastrou orientações estratégicas.", Icons.Default.Map)
                }
            }
        } else {
            items(guidelines) { guide ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = GabSurface),
                    border = BorderStroke(1.dp, GabSecondary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(GabAccent))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(guide.categoria, style = MaterialTheme.typography.labelMedium, color = GabTextSecondary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(guide.titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GabTextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(guide.descricao, style = MaterialTheme.typography.bodySmall, color = GabTextSecondary)
                    }
                }
            }
        }
    }
}

// ── Dialog de Aprovação ───────────────────────────────────────────────────────
@Composable
fun ApproveAndCreateProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (Priority, Risk, String, String, String, Double, Double) -> Unit
) {
    var prioridade by remember { mutableStateOf(Priority.MEDIA) }
    var risco by remember { mutableStateOf(Risk.MEDIO) }
    var area by remember { mutableStateOf("Operações") }
    var dataInicio by remember { mutableStateOf(FormatUtils.removeNonDigits(DateUtils.getCurrentDate())) }
    var dataFim by remember { mutableStateOf(FormatUtils.removeNonDigits(DateUtils.addDaysToCurrentDate(30))) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GabSurface,
        title = { Text("Aprovação Tática", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Preencha os SLAs de execução para converter em projeto.", style = MaterialTheme.typography.labelMedium, color = GabTextSecondary)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Prioridade", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LazyRow {
                    items(Priority.values()) { prio ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = prioridade == prio, onClick = { prioridade = prio })
                            Text(prio.name, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Risco Estimado", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LazyRow {
                    items(Risk.values()) { r ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = risco == r, onClick = { risco = r })
                            Text(r.name, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = area, onValueChange = { area = it }, label = { Text("Área Responsável") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))

                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dataInicio, onValueChange = { dataInicio = FormatUtils.filterDateInput(it) },
                        label = { Text("Início") }, modifier = Modifier.weight(1f),
                        visualTransformation = DateVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = dataFim, onValueChange = { dataFim = FormatUtils.filterDateInput(it) },
                        label = { Text("Fim") }, modifier = Modifier.weight(1f),
                        visualTransformation = DateVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fmtInicio = FormatUtils.formatDateString(dataInicio)
                    val fmtFim = FormatUtils.formatDateString(dataFim)
                    onConfirm(prioridade, risco, area, fmtInicio, fmtFim, 0.0, 0.0)
                },
                colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Aprovar e Criar Projeto", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = GabTextSecondary) }
        }
    )
}
