package com.inovagab.app.data.model

import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val titulo: String,
    val descricao: String,
    val responsavelId: String,
    val responsavelNome: String,
    val status: ProjectStatus = ProjectStatus.EM_ANDAMENTO,
    val etapaAtual: String = "Planejamento",
    val investimento: Double = 0.0,
    val retornoFinanceiro: Double = 0.0,
    val progressoPercentual: Int = 0,
    val prazoFinal: String = "31/12/2026",
    val categoria: String = "Estratégico",
    val tags: List<String> = emptyList(),
    val comparativoYOY: Double = 0.0 // Comparativo Year-Over-Year para o Dashboard (ex: 12.5 para +12.5%)
)

enum class ProjectStatus(val label: String) {
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDO("Concluído"),
    PAUSADO("Pausado"),
    CANCELADO("Cancelado")
}
