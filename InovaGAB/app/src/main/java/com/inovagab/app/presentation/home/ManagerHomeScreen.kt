package com.inovagab.app.presentation.home

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inovagab.app.data.model.*
import com.inovagab.app.presentation.AppViewModelFactory
import com.inovagab.app.ui.components.*
import com.inovagab.app.ui.theme.*
import com.inovagab.app.utils.DateUtils

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
                idea = idea,
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
                    colors = ButtonDefaults.buttonColors(containerColor = GabAccent),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Avaliar", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
                }
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
                StatusPrazoChip(project.statusPrazo)
                PriorityChip(project.prioridade)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(project.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(project.areaResponsavel, style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Prazo Fim", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
                    Text(project.dataFimPrevista, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Progresso", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
                    Text("${project.progressoPercentual}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Dias Restantes", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
                    Text(if(project.diasRestantes < 0) "Atrasado" else "${project.diasRestantes} dias", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = if(project.diasRestantes < 0) StatusError else GabTextPrimary)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = project.progressoPercentual / 100f,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (project.progressoPercentual == 100) StatusSuccess else GabAccent,
                trackColor = GabSecondary
            )
        }
    }
}

@Composable
fun ApproveAndCreateProjectDialog(
    idea: Idea, 
    onDismiss: () -> Unit, 
    onConfirm: (Priority, Risk, String, String, String, Double, Double) -> Unit
) {
    var prioridade by remember { mutableStateOf(Priority.MEDIA) }
    var risco by remember { mutableStateOf(Risk.MEDIO) }
    var area by remember { mutableStateOf("Operações") }
    
    // Simulação simplificada de datas como String para não quebrar API 24
    var dataInicio by remember { mutableStateOf(DateUtils.getCurrentDate()) }
    var dataFim by remember { mutableStateOf(DateUtils.addDaysToCurrentDate(30)) }
    
    var investStr by remember { mutableStateOf("0") }
    var retornoStr by remember { mutableStateOf("0") }

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
                    OutlinedTextField(value = dataInicio, onValueChange = { dataInicio = it }, label = { Text("Início (dd/MM/yyyy)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = dataFim, onValueChange = { dataFim = it }, label = { Text("Fim (dd/MM/yyyy)") }, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = investStr, onValueChange = { investStr = it }, label = { Text("CAPEX/OPEX R$") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = retornoStr, onValueChange = { retornoStr = it }, label = { Text("Retorno R$") }, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val inv = investStr.toDoubleOrNull() ?: 0.0
                    val ret = retornoStr.toDoubleOrNull() ?: 0.0
                    onConfirm(prioridade, risco, area, dataInicio, dataFim, inv, ret) 
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
