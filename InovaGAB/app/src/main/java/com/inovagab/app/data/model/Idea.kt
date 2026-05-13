package com.inovagab.app.data.model

import java.util.UUID

data class Idea(
    val id: String = UUID.randomUUID().toString(),
    val titulo: String,
    val descricao: String,
    val categoria: String,
    val autorId: String,
    val autorNome: String,
    val status: IdeaStatus = IdeaStatus.CADASTRADA,
    val prioridade: Priority = Priority.BAIXA,
    val impactoEsperado: String = "Não avaliado", // Ex: "Redução de 5% no Diesel"
    val economiaGeradaEstimada: Double = 0.0,
    val tags: List<String> = emptyList(),
    val dataCriacao: String = "12/05/2026"
)

enum class IdeaStatus(val label: String) {
    CADASTRADA("Em Fila"),
    EM_ANALISE("Em Análise"),
    PRIORIZADA("Priorizada"),
    APROVADA("Aprovada"),
    REPROVADA("Arquivada"),
    CONVERTIDA_PROJETO("Projeto Ativo")
}

enum class Priority(val label: String) {
    BAIXA("Baixa Prioridade"),
    MEDIA("Média Prioridade"),
    ALTA("Alta Prioridade"),
    CRITICA("Crítica Prioridade")
}
