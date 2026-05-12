package com.inovagab.app.ui.components

import androidx.compose.foundation.background
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
import com.inovagab.app.data.model.*
import com.inovagab.app.ui.theme.*

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
