package com.inovagab.app.data.mock

import com.inovagab.app.data.model.*
import com.inovagab.app.utils.DateUtils

object MockData {
    val users = mutableListOf(
        User("1", "João Operador", "operador@gab.com", "123456", UserProfile.OPERADOR),
        User("2", "Maria Gestora", "gestor@gab.com", "123456", UserProfile.GESTOR),
        User("3", "Carlos Líder", "lider@gab.com", "123456", UserProfile.LIDER)
    )

    val guidelines = mutableListOf(
        StrategicGuideline("1", "Eficiência em Combustível", "Projetos focados em reduzir consumo.", "Operações"),
        StrategicGuideline("2", "Transformação Digital", "Automação de processos internos.", "Tecnologia")
    )

    val ideas = mutableListOf(
        Idea(
            id = "1", 
            titulo = "Telemetria Preditiva", 
            descricao = "Sensores OBD2 para evitar falhas.", 
            categoria = "Tecnologia", 
            autorId = "1", 
            autorNome = "João Operador", 
            status = IdeaStatus.EM_ANALISE,
            prioridade = Priority.ALTA,
            impactoEsperado = "Redução de manutenção",
            economiaGeradaEstimada = 120000.0,
            tags = listOf("IoT", "Manutenção"),
            dataCriacao = DateUtils.addDaysToCurrentDate(-10)
        )
    )

    val projects = mutableListOf(
        Project(
            id = "1", 
            titulo = "Otimização de Rotas Operacionais", 
            descricao = "Uso de roteirização para reduzir KM rodado vazio.", 
            responsavelId = "2",
            responsavelNome = "Maria Gestora",
            areaResponsavel = "Logística",
            etapaAtual = "Execução", 
            status = ProjectStatus.EM_ANDAMENTO,
            prioridade = Priority.ALTA,
            risco = Risk.MEDIO,
            dataInicioPrevista = DateUtils.addDaysToCurrentDate(-30),
            dataFimPrevista = DateUtils.addDaysToCurrentDate(15), // NO_PRAZO
            investimento = 85000.0, 
            retornoFinanceiro = 150000.0,
            economiaEstimada = 65000.0,
            ganhoProdutividade = 12.0,
            progressoPercentual = 65,
            categoria = "Logística",
            tags = listOf("IA", "Rotas"),
            comparativoYOY = 5.0
        ),
        Project(
            id = "2", 
            titulo = "Digitalização do Checklist", 
            descricao = "App para inspeção de veículos.", 
            responsavelId = "2",
            responsavelNome = "Maria Gestora",
            areaResponsavel = "Manutenção",
            etapaAtual = "Resultado", 
            status = ProjectStatus.CONCLUIDO,
            prioridade = Priority.MEDIA,
            risco = Risk.BAIXO,
            dataInicioPrevista = DateUtils.addDaysToCurrentDate(-90),
            dataFimPrevista = DateUtils.addDaysToCurrentDate(-5), // CONCLUIDO
            dataInicioReal = DateUtils.addDaysToCurrentDate(-90),
            dataFimReal = DateUtils.addDaysToCurrentDate(-5),
            investimento = 25000.0, 
            retornoFinanceiro = 80000.0,
            economiaEstimada = 55000.0,
            ganhoProdutividade = 30.0,
            progressoPercentual = 100,
            categoria = "Manutenção",
            tags = listOf("Digital", "Paperless"),
            comparativoYOY = 12.5
        ),
        Project(
            id = "3", 
            titulo = "Redução Consumo de Combustível", 
            descricao = "Treinamento e gamificação para motoristas.", 
            responsavelId = "3",
            responsavelNome = "Carlos Líder",
            areaResponsavel = "Sustentabilidade",
            etapaAtual = "Validação", 
            status = ProjectStatus.EM_ANDAMENTO,
            prioridade = Priority.CRITICA,
            risco = Risk.ALTO,
            dataInicioPrevista = DateUtils.addDaysToCurrentDate(-120),
            dataFimPrevista = DateUtils.addDaysToCurrentDate(-2), // ATRASADO
            investimento = 120000.0, 
            retornoFinanceiro = 500000.0,
            economiaEstimada = 380000.0,
            ganhoProdutividade = 5.0,
            progressoPercentual = 85,
            categoria = "Sustentabilidade",
            tags = listOf("ESG", "Frota"),
            comparativoYOY = 8.2
        ),
        Project(
            id = "4", 
            titulo = "Automação de Triagem Interna", 
            descricao = "Bot para SAC e demandas da garagem.", 
            responsavelId = "1",
            responsavelNome = "João Operador",
            areaResponsavel = "Atendimento",
            etapaAtual = "Diagnóstico", 
            status = ProjectStatus.EM_ANDAMENTO,
            prioridade = Priority.BAIXA,
            risco = Risk.BAIXO,
            dataInicioPrevista = DateUtils.addDaysToCurrentDate(-10),
            dataFimPrevista = DateUtils.addDaysToCurrentDate(5), // PROXIMO_VENCIMENTO
            investimento = 10000.0, 
            retornoFinanceiro = 45000.0,
            economiaEstimada = 35000.0,
            ganhoProdutividade = 40.0,
            progressoPercentual = 30,
            categoria = "Atendimento",
            tags = listOf("Bot", "CX"),
            comparativoYOY = 2.1
        )
    )
}
