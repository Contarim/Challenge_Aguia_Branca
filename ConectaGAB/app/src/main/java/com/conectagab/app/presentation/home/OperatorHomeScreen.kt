package com.conectagab.app.presentation.home

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
import com.conectagab.app.data.model.Idea
import com.conectagab.app.data.model.IdeaStatus
import com.conectagab.app.data.model.StrategicGuideline
import com.conectagab.app.presentation.AppViewModelFactory
import com.conectagab.app.ui.components.*
import com.conectagab.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperatorHomeScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: OperatorViewModel = viewModel(factory = AppViewModelFactory)
) {
    val user by viewModel.currentUser.collectAsState(initial = null)
    val myIdeas by viewModel.myIdeas.collectAsState(initial = emptyList())
    val guidelines by viewModel.guidelines.collectAsState(initial = emptyList())
    var showNewIdeaDialog by remember { mutableStateOf(false) }
    
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(GabAccent), contentAlignment = Alignment.Center) {
                            Text(user?.nome?.take(1) ?: "O", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Workspace Operacional", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GabTextPrimary)
                            Text("Bem-vindo, ${user?.nome ?: ""}", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
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
                    icon = { Icon(Icons.Default.Lightbulb, contentDescription = null) },
                    label = { Text("Meu Funil") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = StatusInfoBg, selectedIconColor = GabAccent, selectedTextColor = GabAccent)
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Map, contentDescription = null) },
                    label = { Text("Diretrizes") },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = StatusInfoBg, selectedIconColor = GabAccent, selectedTextColor = GabAccent)
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = { showNewIdeaDialog = true },
                    icon = { Icon(Icons.Default.Add, "Nova Ideia") },
                    text = { Text("Registrar Ideia", fontWeight = FontWeight.Bold) },
                    containerColor = GabDarkBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(GabBackground)) {
            if (selectedTab == 0) {
                OperatorIdeasContent(myIdeas)
            } else {
                OperatorGuidelinesContent(guidelines)
            }
        }

        if (showNewIdeaDialog) {
            NewIdeaDialog(
                viewModel = viewModel,
                onDismiss = { showNewIdeaDialog = false },
                onSuccess = { showNewIdeaDialog = false }
            )
        }
    }
}

@Composable
fun OperatorIdeasContent(myIdeas: List<Idea>) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item {
            GamificationEnterpriseCard(myIdeas)
            Spacer(modifier = Modifier.height(24.dp))
            
            SectionHeader("Pipeline de Inovação", "Acompanhe o estágio de cada submissão")
            Spacer(modifier = Modifier.height(16.dp))
            
            PipelineOverview(myIdeas)
            
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (myIdeas.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    EmptyState("Nenhuma Ideia", "Sem ideias cadastradas", Icons.Default.Inbox)
                }
            }
        } else {
            items(myIdeas) { idea ->
                IdeaCard(idea = idea)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun OperatorGuidelinesContent(guidelines: List<StrategicGuideline>) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item {
            Text("Diretrizes Estratégicas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GabTextPrimary)
            Text("Alinhe suas ideias aos objetivos da empresa", style = MaterialTheme.typography.bodyMedium, color = GabTextSecondary)
            Spacer(modifier = Modifier.height(24.dp))
        }
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
                    Text(guide.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GabTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(guide.descricao, style = MaterialTheme.typography.bodyMedium, color = GabTextSecondary)
                }
            }
        }
    }
}

@Composable
fun NewIdeaDialog(
    viewModel: OperatorViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val title by viewModel.ideaTitle.collectAsState()
    val desc by viewModel.ideaDesc.collectAsState()
    val cat by viewModel.ideaCat.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GabSurface,
        title = { Text("Registrar Ideia", fontWeight = FontWeight.Bold, color = GabTextPrimary) },
        text = {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = viewModel::onTitleChange,
                    label = { Text("Título da Ideia") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = viewModel::onDescChange,
                    label = { Text("Problema e Solução") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = cat,
                    onValueChange = viewModel::onCatChange,
                    label = { Text("Categoria (Tags)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.submitIdea(onSuccess) }, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = GabDarkBlue)) {
                Text("Submeter", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = GabTextSecondary)
            }
        }
    )
}

@Composable
fun PipelineOverview(ideas: List<Idea>) {
    val cadastrada = ideas.count { it.status == IdeaStatus.CADASTRADA }
    val emAnalise = ideas.count { it.status == IdeaStatus.EM_ANALISE || it.status == IdeaStatus.PRIORIZADA }
    val aprovada = ideas.count { it.status == IdeaStatus.APROVADA || it.status == IdeaStatus.CONVERTIDA_PROJETO }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        PipelineStage("Fila", cadastrada, StatusNeutral)
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GabSecondary, modifier = Modifier.align(Alignment.CenterVertically))
        PipelineStage("Análise", emAnalise, StatusWarning)
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GabSecondary, modifier = Modifier.align(Alignment.CenterVertically))
        PipelineStage("Sucesso", aprovada, StatusSuccess)
    }
}

@Composable
fun PipelineStage(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Text("$count", fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
    }
}

@Composable
fun GamificationEnterpriseCard(myIdeas: List<Idea>) {
    val approved = myIdeas.count { it.status == IdeaStatus.APROVADA || it.status == IdeaStatus.CONVERTIDA_PROJETO }
    val points = (myIdeas.size * 10) + (approved * 50)
    
    val (badgeName, nextLevelPoints, progress) = when {
        points < 100 -> Triple("Semente da Inovação", 100, points / 100f)
        points < 300 -> Triple("Agente de Mudança", 300, (points - 100) / 200f)
        else -> Triple("Visão Águia", points, 1f)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GabDarkBlue),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = "Badge", tint = StatusWarning, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(badgeName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(GabAccent.copy(alpha = 0.2f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("$points XP", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = GabAccent)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = StatusSuccess,
                trackColor = Color.White.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(if(points < 300) "Faltam ${nextLevelPoints - points} XP para o próximo nível" else "Nível Máximo Alcançado!", style = MaterialTheme.typography.labelSmall, color = GabSecondary)
        }
    }
}
