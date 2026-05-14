package com.conectagab.app.data.model

import com.conectagab.app.utils.DateUtils
import java.util.UUID

enum class ProjectStatus(val label: String) {
    PLANEJADO("Planejado"),
    EM_ANDAMENTO("Em andamento"),
    PAUSADO("Pausado"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado")
}

enum class Risk(val label: String) {
    BAIXO("Baixo"),
    MEDIO("Médio"),
    ALTO("Alto"),
    CRITICO("Crítico")
}

enum class StatusPrazo(val label: String) {
    NO_PRAZO("No prazo"),
    PROXIMO_VENCIMENTO("Próximo do vencimento"),
    ATRASADO("Atrasado"),
    CONCLUIDO("Concluído")
}

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val titulo: String,
    val descricao: String,
    val responsavelId: String,
    val responsavelNome: String,
    val areaResponsavel: String,
    val etapaAtual: String = "Planejamento",
    val status: ProjectStatus = ProjectStatus.EM_ANDAMENTO,
    val prioridade: Priority = Priority.MEDIA,
    val risco: Risk = Risk.MEDIO,
    val dataInicioPrevista: String,
    val dataFimPrevista: String,
    val dataInicioReal: String? = null,
    val dataFimReal: String? = null,
    val investimentoEstimado: Double = 0.0,
    val retornoEstimado: Double = 0.0,
    val economiaEstimada: Double = 0.0,
    val ganhoProdutividade: Double = 0.0,
    val progressoPercentual: Int = 0,
    val categoria: String = "Estratégico",
    val tags: List<String> = emptyList(),
    val comparativoYOY: Double = 0.0
) {
    val prazoEmDias: Int
        get() = DateUtils.daysBetween(dataInicioPrevista, dataFimPrevista)

    val diasRestantes: Int
        get() = DateUtils.calculateDaysRemaining(dataFimPrevista)
        
    val lucroEstimado: Double
        get() = retornoEstimado - investimentoEstimado

    val roi: Double
        get() = if (investimentoEstimado > 0) ((retornoEstimado - investimentoEstimado) / investimentoEstimado) * 100 else 0.0

    val percentualPrazoConsumido: Double
        get() {
            val diasDecorridos = DateUtils.daysBetween(dataInicioPrevista, DateUtils.getCurrentDate())
            val prazoTotal = prazoEmDias
            return if (prazoTotal > 0) {
                (diasDecorridos.toDouble() / prazoTotal) * 100
            } else 0.0
        }

    val statusPrazo: StatusPrazo
        get() {
            if (status == ProjectStatus.CONCLUIDO) return StatusPrazo.CONCLUIDO
            val remaining = diasRestantes
            return when {
                remaining < 0 -> StatusPrazo.ATRASADO
                remaining <= 7 -> StatusPrazo.PROXIMO_VENCIMENTO
                else -> StatusPrazo.NO_PRAZO
            }
        }
}
