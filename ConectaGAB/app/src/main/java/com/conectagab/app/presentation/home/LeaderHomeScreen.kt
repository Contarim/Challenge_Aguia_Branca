package com.conectagab.app.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.conectagab.app.utils.CurrencyVisualTransformation
import com.conectagab.app.utils.DateVisualTransformation
import com.conectagab.app.utils.FormatUtils
import androidx.lifecycle.viewmodel.compose.viewModel
import com.conectagab.app.data.model.Project
import com.conectagab.app.data.model.StatusPrazo
import com.conectagab.app.data.model.StrategicGuideline
import com.conectagab.app.presentation.AppViewModelFactory
import com.conectagab.app.ui.components.*
import com.conectagab.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderHomeScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: LeaderViewModel = viewModel(factory = AppViewModelFactory)
) {
    val user by viewModel.currentUser.collectAsState(initial = null)
    val projects by viewModel.projects.collectAsState(initial = emptyList())
    val guidelines by viewModel.guidelines.collectAsState(initial = emptyList())
    var selectedTab by remember { mutableStateOf(0) }
    var projectToEdit by remember { mutableStateOf<Project?>(null) }
    var guidelineToEdit by remember { mutableStateOf<StrategicGuideline?>(null) }
    var showAddGuidelineDialog by remember { mutableStateOf(false) }
    var guidelineToDelete by remember { mutableStateOf<StrategicGuideline?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(GabDarkBlue), contentAlignment = Alignment.Center) {
                            Text(user?.nome?.take(1) ?: "L", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Visão Executiva (C-Level)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GabTextPrimary)
                            Text("SLA e Retorno Financeiro", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
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
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = { Text("Dashboard") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = StatusInfoBg, selectedIconColor = GabAccent, selectedTextColor = GabAccent)
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Portfólio") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = StatusInfoBg, selectedIconColor = GabAccent, selectedTextColor = GabAccent)
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Flag, contentDescription = null) },
                    label = { Text("Diretrizes") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = StatusInfoBg, selectedIconColor = GabAccent, selectedTextColor = GabAccent)
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 2) {
                ExtendedFloatingActionButton(
                    onClick = { showAddGuidelineDialog = true },
                    icon = { Icon(Icons.Default.Add, "Nova Diretriz") },
                    text = { Text("Nova Diretriz", fontWeight = FontWeight.Bold) },
                    containerColor = GabDarkBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(GabBackground)) {
            when (selectedTab) {
                0 -> ExecutiveDashboardV2(projects)
                1 -> PortfolioV2Content(projects) { projectToEdit = it }
                2 -> LeaderGuidelinesContent(
                    guidelines = guidelines,
                    onEdit = { guidelineToEdit = it },
                    onDelete = { guidelineToDelete = it }
                )
            }
        }

        // Editar projeto
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

        // Adicionar / Editar diretriz
        if (showAddGuidelineDialog || guidelineToEdit != null) {
            AddEditGuidelineDialog(
                existing = guidelineToEdit,
                onDismiss = { showAddGuidelineDialog = false; guidelineToEdit = null },
                onSave = { titulo, desc, cat ->
                    if (guidelineToEdit != null) {
                        viewModel.updateGuideline(guidelineToEdit!!.id, titulo, desc, cat)
                    } else {
                        viewModel.addGuideline(titulo, desc, cat)
                    }
                    showAddGuidelineDialog = false
                    guidelineToEdit = null
                }
            )
        }

        // Confirmar exclusão
        guidelineToDelete?.let { g ->
            AlertDialog(
                onDismissRequest = { guidelineToDelete = null },
                containerColor = GabSurface,
                title = { Text("Excluir Diretriz?", fontWeight = FontWeight.Bold) },
                text = { Text("A diretriz \"${g.titulo}\" será removida permanentemente.", color = GabTextSecondary) },
                confirmButton = {
                    Button(
                        onClick = { viewModel.deleteGuideline(g.id); guidelineToDelete = null },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusError)
                    ) { Text("Excluir", color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = { guidelineToDelete = null }) { Text("Cancelar", color = GabTextSecondary) }
                }
            )
        }
    }
}

// ── Dashboard ─────────────────────────────────────────────────────────────────
@Composable
fun ExecutiveDashboardV2(projects: List<Project>) {
    val totalInvest = projects.sumOf { it.investimentoEstimado }
    val totalRetorno = projects.sumOf { it.retornoEstimado }
    val lucroReal = totalRetorno - totalInvest
    val roiGlobal = if (totalInvest > 0) ((totalRetorno - totalInvest) / totalInvest) * 100 else 0.0
    val mediaGanhoProdutividade = if (projects.isNotEmpty()) projects.sumOf { it.ganhoProdutividade } / projects.size else 0.0
    val totalEconomia = projects.sumOf { it.economiaEstimada }
    val atrasados = projects.count { it.statusPrazo == StatusPrazo.ATRASADO }

    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            if (atrasados > 0) {
                ExecutiveAlertCard(
                    titulo = "$atrasados Projeto(s) Atrasado(s)",
                    mensagem = "O portfólio possui entregas fora do prazo. Requer atenção imediata.",
                    icon = Icons.Default.Warning,
                    isError = true
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("KPIs Financeiros", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    KPICard("Lucro Esperado", "R$ ${(lucroReal / 1000).toInt()}k", Icons.Default.AttachMoney, if(lucroReal >= 0) StatusSuccess else StatusError)
                }
                Box(modifier = Modifier.weight(1f)) {
                    KPICard("ROI Global", "${String.format("%.1f", roiGlobal)}%", Icons.Default.TrendingUp, if(roiGlobal >= 0) StatusSuccess else StatusError)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    KPICard("Produtividade", "+${mediaGanhoProdutividade.toInt()}%", Icons.Default.Speed, StatusInfo)
                }
                Box(modifier = Modifier.weight(1f)) {
                    KPICard("Economia Total", "R$ ${(totalEconomia / 1000).toInt()}k", Icons.Default.Savings, StatusSuccess)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Status de SLAs (Prazos)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    KPICard("No Prazo", "${projects.count { it.statusPrazo == StatusPrazo.NO_PRAZO }}", Icons.Default.CheckCircle, StatusSuccess)
                }
                Box(modifier = Modifier.weight(1f)) {
                    KPICard("Risco/Venc.", "${projects.count { it.statusPrazo == StatusPrazo.PROXIMO_VENCIMENTO }}", Icons.Default.Schedule, StatusWarning)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Top Projetos por ROI", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
        }

        val topProjects = projects.sortedByDescending { it.lucroEstimado }.take(3)
        items(topProjects) { proj ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = GabSurface),
                border = BorderStroke(1.dp, GabSecondary)
            ) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(proj.titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GabTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(proj.areaResponsavel, style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
                    }
                    val roi = if(proj.investimentoEstimado > 0) (proj.lucroEstimado / proj.investimentoEstimado) * 100 else 0.0
                    Text("ROI: ${String.format("%.1f", roi)}%", color = StatusSuccess, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun KPICard(title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = GabSurface),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, GabSecondary)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, style = MaterialTheme.typography.labelMedium, color = GabTextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GabTextPrimary)
        }
    }
}

// ── Portfólio ─────────────────────────────────────────────────────────────────
@Composable
fun PortfolioV2Content(projects: List<Project>, onProjectClick: (Project) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Text("Portfólio Executivo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Text("Toque em um projeto para editar dados e resultados", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(projects) { project ->
            ProjectCard(project = project, onClick = { onProjectClick(project) })
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// ── Diretrizes CRUD ───────────────────────────────────────────────────────────
@Composable
fun LeaderGuidelinesContent(
    guidelines: List<StrategicGuideline>,
    onEdit: (StrategicGuideline) -> Unit,
    onDelete: (StrategicGuideline) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Text("Orientações Estratégicas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Text("Gerencie as diretrizes visíveis a todo o time", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (guidelines.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    EmptyState("Nenhuma Diretriz", "Use o botão (+) para criar a primeira orientação estratégica.", Icons.Default.Flag)
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
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(guide.categoria, style = MaterialTheme.typography.labelSmall, color = GabAccent, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(guide.titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GabTextPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(guide.descricao, style = MaterialTheme.typography.bodySmall, color = GabTextSecondary)
                            }
                            Column {
                                IconButton(onClick = { onEdit(guide) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = GabAccent, modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { onDelete(guide) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = StatusError, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) } // espaço para o FAB
    }
}

// ── Dialog Adicionar/Editar Diretriz ──────────────────────────────────────────
@Composable
fun AddEditGuidelineDialog(
    existing: StrategicGuideline? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var titulo by remember { mutableStateOf(existing?.titulo ?: "") }
    var descricao by remember { mutableStateOf(existing?.descricao ?: "") }
    var categoria by remember { mutableStateOf(existing?.categoria ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GabSurface,
        title = {
            Text(
                if (existing == null) "Nova Diretriz" else "Editar Diretriz",
                fontWeight = FontWeight.Bold,
                color = GabTextPrimary
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título da Diretriz") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição / Orientação") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoria (ex: Operações, ESG...)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (titulo.isNotBlank()) onSave(titulo, descricao, categoria) },
                colors = ButtonDefaults.buttonColors(containerColor = GabDarkBlue),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Salvar", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = GabTextSecondary) }
        }
    )
}

// ── Dialog Compartilhado de Edição de Projeto ─────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedEditProjectDialog(
    project: Project,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, Double, Double, com.conectagab.app.data.model.ProjectStatus, Int, String, Double, Double) -> Unit
) {
    var responsavel by remember { mutableStateOf(project.responsavelNome) }
    var area by remember { mutableStateOf(project.areaResponsavel) }
    var etapa by remember { mutableStateOf(project.etapaAtual) }
    var dataInicio by remember { mutableStateOf(FormatUtils.removeNonDigits(project.dataInicioPrevista)) }
    var dataFim by remember { mutableStateOf(FormatUtils.removeNonDigits(project.dataFimPrevista)) }
    var investStr by remember { mutableStateOf(FormatUtils.doubleToCentsString(project.investimentoEstimado)) }
    var retornoStr by remember { mutableStateOf(FormatUtils.doubleToCentsString(project.retornoEstimado)) }
    var economiaStr by remember { mutableStateOf(FormatUtils.doubleToCentsString(project.economiaEstimada)) }
    var produtividadeStr by remember { mutableStateOf(project.ganhoProdutividade.toInt().toString()) }
    var progresso by remember { mutableStateOf(project.progressoPercentual.toFloat()) }
    var status by remember { mutableStateOf(project.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GabSurface,
        title = { Text("Editar Projeto", fontWeight = FontWeight.Bold, color = GabTextPrimary) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = responsavel, onValueChange = { responsavel = it }, label = { Text("Responsável") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = area, onValueChange = { area = it }, label = { Text("Grupo / Área") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = etapa, onValueChange = { etapa = it }, label = { Text("Etapa Atual") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
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
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = investStr, onValueChange = { investStr = FormatUtils.removeNonDigits(it) },
                        label = { Text("CAPEX") }, modifier = Modifier.weight(1f),
                        visualTransformation = CurrencyVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = retornoStr, onValueChange = { retornoStr = FormatUtils.removeNonDigits(it) },
                        label = { Text("Retorno") }, modifier = Modifier.weight(1f),
                        visualTransformation = CurrencyVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = economiaStr, onValueChange = { economiaStr = FormatUtils.removeNonDigits(it) },
                        label = { Text("Economia Real") }, modifier = Modifier.weight(1f),
                        visualTransformation = CurrencyVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = produtividadeStr, onValueChange = { produtividadeStr = it.filter { c -> c.isDigit() }.take(3) },
                        label = { Text("Produtividade %") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Progresso: ${progresso.toInt()}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
                Slider(value = progresso, onValueChange = { progresso = it }, valueRange = 0f..100f, steps = 19, colors = SliderDefaults.colors(thumbColor = GabDarkBlue, activeTrackColor = GabDarkBlue))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Status do Projeto", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = GabTextPrimary)
                LazyRow {
                    items(com.conectagab.app.data.model.ProjectStatus.values()) { s ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = status == s, onClick = { status = s })
                            Text(s.label, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val inv = FormatUtils.formatToDouble(investStr)
                    val ret = FormatUtils.formatToDouble(retornoStr)
                    val eco = FormatUtils.formatToDouble(economiaStr)
                    val prod = produtividadeStr.toDoubleOrNull() ?: project.ganhoProdutividade
                    val fmtInicio = FormatUtils.formatDateString(dataInicio)
                    val fmtFim = FormatUtils.formatDateString(dataFim)
                    onSave(project.id, responsavel, area, fmtInicio, fmtFim, inv, ret, status, progresso.toInt(), etapa, eco, prod)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GabDarkBlue)
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = GabTextSecondary) }
        }
    )
}
