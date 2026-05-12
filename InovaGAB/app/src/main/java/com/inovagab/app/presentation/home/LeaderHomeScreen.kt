package com.inovagab.app.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inovagab.app.data.model.Project
import com.inovagab.app.data.model.StatusPrazo
import com.inovagab.app.presentation.AppViewModelFactory
import com.inovagab.app.ui.components.*
import com.inovagab.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderHomeScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: LeaderViewModel = viewModel(factory = AppViewModelFactory)
) {
    val user by viewModel.currentUser.collectAsState(initial = null)
    val projects by viewModel.projects.collectAsState(initial = emptyList())
    var selectedTab by remember { mutableStateOf(0) }
    var projectDetailsToView by remember { mutableStateOf<Project?>(null) }

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
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(GabBackground)) {
            if (selectedTab == 0) {
                ExecutiveDashboardV2(projects)
            } else {
                PortfolioV2Content(projects) { projectDetailsToView = it }
            }
        }

        projectDetailsToView?.let { proj ->
            ProjectDetailsDialog(proj) { projectDetailsToView = null }
        }
    }
}

@Composable
fun ExecutiveDashboardV2(projects: List<Project>) {
    val totalInvest = projects.sumOf { it.investimento }
    val totalRetorno = projects.sumOf { it.retornoFinanceiro }
    val lucroReal = totalRetorno - totalInvest
    val roiGlobal = if (totalInvest > 0) ((totalRetorno - totalInvest) / totalInvest) * 100 else 0.0

    val atrasados = projects.count { it.statusPrazo == StatusPrazo.ATRASADO }
    val risco = projects.count { it.risco == com.inovagab.app.data.model.Risk.CRITICO || it.risco == com.inovagab.app.data.model.Risk.ALTO }

    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            if (atrasados > 0) {
                ExecutiveAlertCard(
                    titulo = "$atrasados Projetos Atrasados",
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
                    KPICard("Lucro Esperado", "R$ ${lucroReal.toInt() / 1000}k", Icons.Default.AttachMoney, if(lucroReal >= 0) StatusSuccess else StatusError, "")
                }
                Box(modifier = Modifier.weight(1f)) {
                    KPICard("ROI Global", "${String.format("%.1f", roiGlobal)}%", Icons.Default.TrendingUp, if(roiGlobal >= 0) StatusSuccess else StatusError, "")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Status de SLAs (Prazos)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    KPICard("No Prazo", "${projects.count { it.statusPrazo == StatusPrazo.NO_PRAZO }}", Icons.Default.CheckCircle, StatusSuccess, "")
                }
                Box(modifier = Modifier.weight(1f)) {
                    KPICard("Risco/Venc.", "${projects.count { it.statusPrazo == StatusPrazo.PROXIMO_VENCIMENTO }}", Icons.Default.Schedule, StatusWarning, "")
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
                    val roi = if(proj.investimento>0) (proj.lucroEstimado/proj.investimento)*100 else 0.0
                    Text("ROI: ${String.format("%.1f", roi)}%", color = StatusSuccess, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun KPICard(title: String, value: String, icon: ImageVector, color: Color, comparison: String) {
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

@Composable
fun PortfolioV2Content(projects: List<Project>, onProjectClick: (Project) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Text("Portfólio Executivo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(projects) { project ->
            Box(modifier = Modifier.clickable { onProjectClick(project) }) {
                EnterpriseProjectCard(project)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ProjectDetailsDialog(project: Project, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GabSurface,
        title = { Text(project.titulo, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusPrazoChip(project.statusPrazo)
                    RiskChip(project.risco)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Responsável: ${project.responsavelNome} (${project.areaResponsavel})", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Cronograma", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Text("Início Previsto: ${project.dataInicioPrevista}", style = MaterialTheme.typography.bodySmall)
                Text("Fim Previsto: ${project.dataFimPrevista}", style = MaterialTheme.typography.bodySmall)
                Text("Prazo em Dias: ${project.prazoEmDias}", style = MaterialTheme.typography.bodySmall)
                Text("Dias Restantes: ${project.diasRestantes}", style = MaterialTheme.typography.bodySmall, color = if(project.diasRestantes < 0) StatusError else GabTextPrimary)
                
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Financeiro", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Text("Investimento: R$ ${project.investimento}", style = MaterialTheme.typography.bodySmall)
                Text("Retorno (Lucro): R$ ${project.lucroEstimado}", style = MaterialTheme.typography.bodySmall, color = StatusSuccess)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = GabDarkBlue)) {
                Text("Fechar")
            }
        }
    )
}
