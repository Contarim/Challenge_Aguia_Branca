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
    
    var selectedTab by remember { mutableStateOf(0) }
    var ideaToApprove by remember { mutableStateOf<Idea?>(null) }

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
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(GabBackground)) {
            if (selectedTab == 0) {
                ManagerIdeasFunnel(ideas, onApproveClick = { ideaToApprove = it })
            } else {
                ManagerProjectsContent(projects)
            }
        }

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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerIdeasFunnel(ideas: List<Idea>, onApproveClick: (Idea) -> Unit) {
    val pendingIdeas = ideas.filter { it.status == IdeaStatus.CADASTRADA || it.status == IdeaStatus.EM_ANALISE }
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
                    Text("Nenhuma ideia nesta categoria.", color = GabTextSecondary)
                }
            }
        } else {
            items(filteredIdeas) { idea ->
                IdeaCard(idea = idea, onClick = { onApproveClick(idea) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ManagerProjectsContent(projects: List<Project>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Text("Gestão de Execução (Projetos)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (projects.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Nenhum projeto rodando.", color = GabTextSecondary)
                }
            }
        } else {
            items(projects.sortedBy { it.statusPrazo.ordinal }) { project ->
                ProjectCard(project = project, onClick = {})
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ApproveAndCreateProjectDialog(
    onDismiss: () -> Unit, 
    onConfirm: (Priority, Risk, String, String, String, Double, Double) -> Unit
) {
    var prioridade by remember { mutableStateOf(Priority.MEDIA) }
    var risco by remember { mutableStateOf(Risk.MEDIO) }
    var area by remember { mutableStateOf("Operações") }
    
    // Armazenamos apenas os dígitos numéricos no estado
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
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Área Responsável") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dataInicio, 
                        onValueChange = { dataInicio = FormatUtils.filterDateInput(it) }, 
                        label = { Text("Início") }, 
                        modifier = Modifier.weight(1f),
                        visualTransformation = DateVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = dataFim, 
                        onValueChange = { dataFim = FormatUtils.filterDateInput(it) }, 
                        label = { Text("Fim") }, 
                        modifier = Modifier.weight(1f),
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
            ) {
                Text("Aprovar e Criar Projeto", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = GabTextSecondary) }
        }
    )
}
