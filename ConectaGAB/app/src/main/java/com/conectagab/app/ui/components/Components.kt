package com.conectagab.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.conectagab.app.data.model.*
import com.conectagab.app.ui.theme.*

@Composable
fun StatusPrazoChip(statusPrazo: StatusPrazo) {
    val (bgColor, textColor, icon) = when (statusPrazo) {
        StatusPrazo.NO_PRAZO -> Triple(StatusSuccessBg, StatusSuccess, Icons.Default.CheckCircle)
        StatusPrazo.PROXIMO_VENCIMENTO -> Triple(StatusWarningBg, StatusWarning, Icons.Default.Schedule)
        StatusPrazo.ATRASADO -> Triple(StatusErrorBg, StatusError, Icons.Default.Warning)
        StatusPrazo.CONCLUIDO -> Triple(StatusNeutralBg, StatusNeutral, Icons.Default.DoneAll)
    }
    
    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(bgColor).padding(horizontal = 6.dp, vertical = 2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(statusPrazo.label, style = MaterialTheme.typography.labelSmall, color = textColor, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
    }
}

@Composable
fun RiskChip(risk: Risk) {
    val (bgColor, textColor) = when (risk) {
        Risk.BAIXO -> StatusSuccessBg to StatusSuccess
        Risk.MEDIO -> StatusInfoBg to StatusInfo
        Risk.ALTO -> StatusWarningBg to StatusWarning
        Risk.CRITICO -> StatusErrorBg to StatusError
    }
    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(bgColor).padding(horizontal = 6.dp, vertical = 2.dp)) {
        Text("Risco: ${risk.label}", style = MaterialTheme.typography.labelSmall, color = textColor, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}

@Composable
fun PriorityChip(priority: Priority) {
    val (bgColor, textColor) = when (priority) {
        Priority.BAIXA -> StatusNeutralBg to StatusNeutral
        Priority.MEDIA -> StatusInfoBg to StatusInfo
        Priority.ALTA -> StatusWarningBg to StatusWarning
        Priority.CRITICA -> StatusErrorBg to StatusError
    }
    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(bgColor).padding(horizontal = 6.dp, vertical = 2.dp)) {
        Text(priority.label, style = MaterialTheme.typography.labelSmall, color = textColor, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}

@Composable
fun ExecutiveAlertCard(titulo: String, mensagem: String, icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Info, isError: Boolean = false) {
    val bgColor = if (isError) StatusErrorBg else StatusWarningBg
    val iconColor = if (isError) StatusError else StatusWarning
    val textColor = GabTextPrimary

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(titulo, style = MaterialTheme.typography.titleSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = textColor)
                Text(mensagem, style = MaterialTheme.typography.bodySmall, color = textColor.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = GabTextPrimary)
        if (subtitle != null) {
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = GabTextSecondary)
        }
    }
}

@Composable
fun EmptyState(title: String, message: String, icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Inbox) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = GabTextSecondary.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = GabTextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = GabTextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
fun ProgressBar(progress: Int, modifier: Modifier = Modifier) {
    val progressFloat = (progress.coerceIn(0, 100)) / 100f
    val color = when {
        progress < 30 -> StatusError
        progress < 70 -> StatusWarning
        else -> StatusSuccess
    }
    
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Progresso", style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
            Text("$progress%", style = MaterialTheme.typography.labelSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = GabTextPrimary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = progressFloat,
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = GabSecondary
        )
    }
}

@Composable
fun DeadlineChip(daysRemaining: Int) {
    val (bgColor, textColor, text) = when {
        daysRemaining < 0 -> Triple(StatusErrorBg, StatusError, "${-daysRemaining} dias atrasado")
        daysRemaining == 0 -> Triple(StatusWarningBg, StatusWarning, "Vence hoje")
        daysRemaining <= 7 -> Triple(StatusWarningBg, StatusWarning, "$daysRemaining dias restantes")
        else -> Triple(StatusNeutralBg, StatusNeutral, "$daysRemaining dias restantes")
    }
    
    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(bgColor).padding(horizontal = 6.dp, vertical = 2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Event, contentDescription = null, tint = textColor, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, style = MaterialTheme.typography.labelSmall, color = textColor, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
    }
}

@Composable
fun KpiCard(title: String, value: String, subtitle: String? = null, icon: androidx.compose.ui.graphics.vector.ImageVector, iconColor: Color = GabAccent, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GabSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(title, style = MaterialTheme.typography.bodyMedium, color = GabTextSecondary)
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(iconColor.copy(alpha = 0.1f)).padding(6.dp)) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = GabTextPrimary)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = GabTextSecondary)
            }
        }
    }
}

@Composable
fun ProjectCard(project: Project, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = GabSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(project.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = GabTextPrimary)
                    Text(project.areaResponsavel, style = MaterialTheme.typography.bodySmall, color = GabTextSecondary)
                }
                StatusPrazoChip(project.statusPrazo)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(project.descricao, style = MaterialTheme.typography.bodyMedium, color = GabTextPrimary, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PriorityChip(project.prioridade)
                RiskChip(project.risco)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (project.status != ProjectStatus.CONCLUIDO) {
                DeadlineChip(project.diasRestantes)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            ProgressBar(progress = project.progressoPercentual)
            
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = GabSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = GabTextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(project.responsavelNome, style = MaterialTheme.typography.bodySmall, color = GabTextSecondary)
                }
                Text(project.status.label, style = MaterialTheme.typography.labelMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = GabAccent)
            }
        }
    }
}

@Composable
fun IdeaCard(idea: Idea, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = GabSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(idea.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = GabTextPrimary)
                    Text(idea.categoria, style = MaterialTheme.typography.bodySmall, color = GabTextSecondary)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(GabSecondary).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(idea.status.label, style = MaterialTheme.typography.labelSmall, color = GabTextPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(idea.descricao, style = MaterialTheme.typography.bodyMedium, color = GabTextPrimary, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PriorityChip(idea.prioridade)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = GabTextSecondary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(idea.autorNome, style = MaterialTheme.typography.bodySmall, color = GabTextSecondary)
            }
        }
    }
}

@Composable
fun Timeline(etapas: List<String>, etapaAtual: String) {
    val currentIndex = etapas.indexOf(etapaAtual)
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        etapas.forEachIndexed { index, etapa ->
            val isCompleted = index < currentIndex
            val isCurrent = index == currentIndex
            val color = if (isCompleted || isCurrent) GabAccent else GabSecondary
            val textColor = if (isCurrent) GabTextPrimary else GabTextSecondary
            val fontWeight = if (isCurrent) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(24.dp)) {
                    Box(modifier = Modifier.size(12.dp).clip(androidx.compose.foundation.shape.CircleShape).background(color))
                    if (index < etapas.size - 1) {
                        Box(modifier = Modifier.width(2.dp).height(24.dp).background(color))
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(etapa, style = MaterialTheme.typography.bodyMedium, color = textColor, fontWeight = fontWeight)
            }
        }
    }
}
