package com.inovagab.app.data.mock

import com.inovagab.app.data.model.*

object MockData {
    val users = mutableListOf(
        User("1", "João Operador", "operador@gab.com", "123456", UserProfile.OPERADOR),
        User("2", "Maria Gestora", "gestor@gab.com", "123456", UserProfile.GESTOR),
        User("3", "Carlos Líder", "lider@gab.com", "123456", UserProfile.LIDER)
    )

    val guidelines = mutableListOf(
        StrategicGuideline("1", "Eficiência em Combustível", "Projetos e ideias focadas em reduzir o consumo de diesel na frota.", "Operações"),
        StrategicGuideline("2", "Transformação Digital", "Automação de processos internos e fim do uso de papel.", "Tecnologia"),
        StrategicGuideline("3", "Experiência do Passageiro", "Iniciativas para melhorar o conforto e pontualidade.", "Atendimento")
    )

    val ideas = mutableListOf(
        Idea(
            id = "1", 
            titulo = "Telemetria Preditiva nos Motores", 
            descricao = "Instalar sensores OBD2 que preveem falhas antes delas ocorrerem, evitando ônibus parados na estrada.", 
            categoria = "Tecnologia", 
            autorId = "1", 
            autorNome = "João Operador", 
            status = IdeaStatus.EM_ANALISE,
            prioridade = Priority.ALTA,
            impactoEsperado = "Redução de 15% em manutenções corretivas",
            economiaGeradaEstimada = 120000.0,
            tags = listOf("IoT", "Manutenção", "Frota")
        ),
        Idea(
            id = "2", 
            titulo = "Reaproveitamento de Água na Lavagem", 
            descricao = "Criar um sistema de filtragem nas garagens para reutilizar a água que lava a frota.", 
            categoria = "Sustentabilidade", 
            autorId = "1", 
            autorNome = "João Operador", 
            status = IdeaStatus.APROVADA, 
            prioridade = Priority.MEDIA,
            impactoEsperado = "Queda de 40% na conta de água",
            economiaGeradaEstimada = 45000.0,
            tags = listOf("ESG", "Custos", "Garagem")
        ),
        Idea(
            id = "3", 
            titulo = "Totem de Autoatendimento Rápido", 
            descricao = "Colocar totens nas rodoviárias para embarque via QR Code sem passar no guichê.", 
            categoria = "Atendimento", 
            autorId = "4", 
            autorNome = "Ana Atendente", 
            status = IdeaStatus.CADASTRADA, 
            prioridade = Priority.BAIXA,
            impactoEsperado = "Redução de filas",
            economiaGeradaEstimada = 0.0,
            tags = listOf("CX", "Rodoviária")
        )
    )

    val projects = mutableListOf(
        Project(
            id = "1", 
            titulo = "Automação de Escala de Motoristas", 
            descricao = "Uso de algoritmo de roteirização para otimizar as escalas e reduzir horas extras.", 
            responsavelId = "2",
            responsavelNome = "Maria Gestora", 
            etapaAtual = "Desenvolvimento de Software", 
            status = ProjectStatus.EM_ANDAMENTO, 
            investimento = 85000.0, 
            retornoFinanceiro = 0.0, // Ainda em andamento, retorno não realizado integralmente
            progressoPercentual = 65,
            prazoFinal = "15/10/2026",
            categoria = "Tecnologia",
            tags = listOf("IA", "RH", "Opex"),
            comparativoYOY = 0.0
        ),
        Project(
            id = "2", 
            titulo = "Eco Frota: Transição Elétrica", 
            descricao = "Substituição de 5% da frota urbana por ônibus elétricos.", 
            responsavelId = "3",
            responsavelNome = "Carlos Líder", 
            etapaAtual = "Mensuração de Resultados", 
            status = ProjectStatus.CONCLUIDO, 
            investimento = 2500000.0, 
            retornoFinanceiro = 3100000.0, 
            progressoPercentual = 100,
            prazoFinal = "01/03/2026",
            categoria = "Sustentabilidade",
            tags = listOf("ESG", "Capex", "Frota"),
            comparativoYOY = 14.5
        ),
        Project(
            id = "3", 
            titulo = "App do Cliente 2.0", 
            descricao = "Reformulação do aplicativo de venda de passagens com programa de fidelidade.", 
            responsavelId = "2",
            responsavelNome = "Maria Gestora", 
            etapaAtual = "Testes A/B", 
            status = ProjectStatus.EM_ANDAMENTO, 
            investimento = 120000.0, 
            retornoFinanceiro = 35000.0, 
            progressoPercentual = 80,
            prazoFinal = "30/08/2026",
            categoria = "Vendas",
            tags = listOf("CX", "Receita", "Mobile"),
            comparativoYOY = 5.2
        )
    )
}
