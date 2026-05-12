package com.inovagab.app.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inovagab.app.data.model.*
import com.inovagab.app.presentation.AppViewModelFactory
import com.inovagab.app.ui.theme.*

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
    var selectedIdeaToApprove by remember { mutableStateOf<Idea?>(null) }
    var showProjectDialog by remember { mutableStateOf(false) }
    var automatedProjectIdea by remember { mutableStateOf<Idea?>(null) }

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
                    label = { Text("Funil de Ideias") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = StatusInfoBg, selectedIconColor = GabAccent, selectedTextColor = GabAccent)
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.AccountTree, contentDescription = null) },
                    label = { Text("Projetos Ativos") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = StatusInfoBg, selectedIconColor = GabAccent, selectedTextColor = GabAccent)
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                ExtendedFloatingActionButton(
                    onClick = { showProjectDialog = true },
                    icon = { Icon(Icons.Default.Add, "Novo Projeto") },
                    text = { Text("Novo Projeto", fontWeight = FontWeight.Bold) },
                    containerColor = GabDarkBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(GabBackground)) {
            if (selectedTab == 0) {
                ManagerIdeasFunnel(ideas, onApproveClick = { selectedIdeaToApprove = it })
            } else {
                ManagerProjectsContent(projects)
            }
        }

        selectedIdeaToApprove?.let { idea ->
            ApproveIdeaDialog(
                idea = idea,
                onDismiss = { selectedIdeaToApprove = null },
                onApprove = { priority ->
                    viewModel.approveIdea(idea.id, priority)
                    selectedIdeaToApprove = null
                    automatedProjectIdea = idea
                }
            )
        }

        if (showProjectDialog || automatedProjectIdea != null) {
            val initialTitle = automatedProjectIdea?.titulo ?: ""
            val initialDesc = automatedProjectIdea?.descricao ?: ""
            
            NewProjectDialog(
                initialTitle = initialTitle,
                initialDesc = initialDesc,
                onDismiss = { 
                    showProjectDialog = false
                    automatedProjectIdea = null
                },
                onCreate = { t, d, i ->
                    viewModel.createProject(t, d, i)
                    showProjectDialog = false
                    automatedProjectIdea = null
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
            Text("${pendingIdeas.size} ideias aguardam sua curadoria.", style = MaterialTheme.typography.labelMedium, color = GabTextSecondary)
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
                CuratorshipIdeaCard(idea, onApproveClick)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CuratorshipIdeaCard(idea: Idea, onApproveClick: (Idea) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = GabSurface),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, GabSecondary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(GabSecondary), contentAlignment = Alignment.Center) {
                        Text(idea.autorNome.take(1), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GabTextPrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(idea.autorNome, style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
                }
                Text(idea.dataCriacao, style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(idea.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(idea.descricao, style = MaterialTheme.typography.bodySmall, color = GabTextSecondary)
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row {
                    idea.tags.forEach { tag ->
                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(StatusInfoBg).padding(horizontal = 6.dp, vertical = 2.dp).padding(end = 4.dp)) {
                            Text(tag, style = MaterialTheme.typography.labelSmall, color = StatusInfo, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Button(
                    onClick = { onApproveClick(idea) },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Aprovar", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ManagerProjectsContent(projects: List<Project>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Text("Projetos Ativos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (projects.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Nenhum projeto rodando.", color = GabTextSecondary)
                }
            }
        } else {
            items(projects) { project ->
                EnterpriseProjectCard(project)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun EnterpriseProjectCard(project: Project) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = GabSurface),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, GabSecondary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(project.etapaAtual.uppercase(), style = MaterialTheme.typography.labelSmall, color = GabAccent, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(if (project.progressoPercentual == 100) StatusSuccessBg else StatusInfoBg).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("${project.progressoPercentual}%", style = MaterialTheme.typography.labelSmall, color = if (project.progressoPercentual == 100) StatusSuccess else StatusInfo, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(project.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = project.progressoPercentual / 100f,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (project.progressoPercentual == 100) StatusSuccess else GabAccent,
                trackColor = GabSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Prazo", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
                    Text(project.prazoFinal, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Investimento", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
                    Text("R$ ${project.investimento.toInt()}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
                }
            }
        }
    }
}

@Composable
fun ApproveIdeaDialog(idea: Idea, onDismiss: () -> Unit, onApprove: (Priority) -> Unit) {
    var selectedPriority by remember { mutableStateOf(Priority.MEDIA) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GabSurface,
        title = { Text("Aprovar: ${idea.titulo}", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Defina a prioridade de execução.", color = GabTextSecondary)
                Spacer(modifier = Modifier.height(16.dp))
                Priority.values().forEach { prio ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedPriority == prio, onClick = { selectedPriority = prio }, colors = RadioButtonDefaults.colors(selectedColor = GabAccent))
                        Text(prio.label)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onApprove(selectedPriority) }, colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess), shape = RoundedCornerShape(8.dp)) {
                Text("Confirmar Aprovação", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = GabTextSecondary) }
        }
    )
}

@Composable
fun NewProjectDialog(initialTitle: String = "", initialDesc: String = "", onDismiss: () -> Unit, onCreate: (String, String, Double) -> Unit) {
    var title by remember { mutableStateOf(initialTitle) }
    var desc by remember { mutableStateOf(initialDesc) }
    var invest by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GabSurface,
        title = { Text("Novo Projeto Estruturado", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descrição Executiva") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = invest,
                    onValueChange = { invest = it },
                    label = { Text("Investimento Previsto (CAPEX/OPEX)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(title, desc, invest.toDoubleOrNull() ?: 0.0) },
                colors = ButtonDefaults.buttonColors(containerColor = GabDarkBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Criar Projeto")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = GabTextSecondary) }
        }
    )
}
