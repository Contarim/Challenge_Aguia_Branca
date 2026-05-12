package com.inovagab.app.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.inovagab.app.data.model.ProjectStatus
import com.inovagab.app.presentation.AppViewModelFactory
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
                            Text("Indicadores e Projetos", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
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
                ExecutiveDashboardContent(projects)
            } else {
                PortfolioContent(projects)
            }
        }
    }
}

@Composable
fun ExecutiveDashboardContent(projects: List<Project>) {
    val totalInvest = projects.sumOf { it.investimento }
    val totalRetorno = projects.sumOf { it.retornoFinanceiro }
    val lucroReal = totalRetorno - totalInvest
    
    // Evita divisão por zero
    val roiGlobal = if (totalInvest > 0) ((totalRetorno - totalInvest) / totalInvest) * 100 else 0.0

    val concluidos = projects.count { it.status == ProjectStatus.CONCLUIDO }
    val andamento = projects.count { it.status == ProjectStatus.EM_ANDAMENTO }

    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Text("Overview Financeiro (YTD)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    KPICard("ROI Global", "${String.format("%.1f", roiGlobal)}%", Icons.Default.TrendingUp, StatusSuccess, "+4.2% vs T3")
                }
                Box(modifier = Modifier.weight(1f)) {
                    KPICard("Lucro Líquido", "R$ ${lucroReal.toInt() / 1000}k", Icons.Default.AttachMoney, if(lucroReal >= 0) StatusSuccess else StatusError, if(lucroReal>=0) "+12% YoY" else "-5% YoY")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    KPICard("Em Andamento", "$andamento", Icons.Default.Autorenew, GabAccent, "No prazo")
                }
                Box(modifier = Modifier.weight(1f)) {
                    KPICard("Concluídos", "$concluidos", Icons.Default.CheckCircle, GabDarkBlue, "Neste ano")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Distribuição de Portfólio (CAPEX vs OPEX)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Gráfico Atualizado
            ProjectsBarChart(concluidos, andamento)
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Top Projetos por Impacto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
        }

        val topProjects = projects.sortedByDescending { it.retornoFinanceiro }.take(3)
        items(topProjects) { proj ->
            TopProjectCard(proj)
            Spacer(modifier = Modifier.height(8.dp))
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
                Text(title, style = MaterialTheme.typography.labelMedium, color = GabTextSecondary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            
            val isPositive = comparison.startsWith("+")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if(isPositive) Icons.Default.ArrowUpward else if(comparison.startsWith("-")) Icons.Default.ArrowDownward else Icons.Default.Remove, 
                    contentDescription = null, 
                    tint = if(isPositive) StatusSuccess else if(comparison.startsWith("-")) StatusError else GabTextSecondary, 
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(comparison, style = MaterialTheme.typography.labelSmall, color = if(isPositive) StatusSuccess else if(comparison.startsWith("-")) StatusError else GabTextSecondary)
            }
        }
    }
}

@Composable
fun ProjectsBarChart(concluidos: Int, emAndamento: Int) {
    val total = (concluidos + emAndamento).toFloat().coerceAtLeast(1f)
    val concluidosHeight = concluidos / total
    val emAndamentoHeight = emAndamento / total

    Card(
        modifier = Modifier.fillMaxWidth().height(180.dp),
        colors = CardDefaults.cardColors(containerColor = GabSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, GabSecondary),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            ChartBar(label = "Concluídos", heightRatio = concluidosHeight, color = GabDarkBlue, value = concluidos)
            ChartBar(label = "Em Andamento", heightRatio = emAndamentoHeight, color = GabAccent, value = emAndamento)
        }
    }
}

@Composable
fun ChartBar(label: String, heightRatio: Float, color: Color, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxHeight()) {
        Text("$value", fontWeight = FontWeight.Bold, color = color)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight(heightRatio.coerceAtLeast(0.1f))
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
    }
}

@Composable
fun TopProjectCard(project: Project) {
    val roi = if (project.investimento > 0) ((project.retornoFinanceiro - project.investimento) / project.investimento) * 100 else 0.0
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GabSurface),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, GabSecondary)
    ) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(project.titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GabTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(project.categoria, style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("ROI: ${String.format("%.1f", roi)}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = if(roi >= 0) StatusSuccess else StatusError)
                Text(if(project.comparativoYOY > 0) "+${project.comparativoYOY}% YoY" else "${project.comparativoYOY}% YoY", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
            }
        }
    }
}

@Composable
fun PortfolioContent(projects: List<Project>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Text("Portfólio de Inovação", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(projects) { project ->
            EnterpriseProjectCard(project) // Reusa o componente corporativo que criamos na tela do Gestor
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
