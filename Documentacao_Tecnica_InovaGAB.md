# InovaGAB - Documentação Técnica Oficial (Sprint 1 - Versão Enterprise)

## 1. Visão Geral (O que é e o que NÃO é)
O InovaGAB é um protótipo nativo para Android construído como um MVP corporativo focado na experiência de usuário (UX) e demonstração de fluxo de Gestão de Inovação.

> [!WARNING]
> **O que o App NÃO TEM nesta versão:**
> - Não possui backend, API ou Firebase.
> - Não possui banco de dados persistente (SQLite/Room). Se fechar o app, as novas ideias não são salvas no disco.
> - Não possui autenticação real (senhas não são criptografadas, login é feito varrendo uma lista mockada em memória).
> - Não possui integração com câmeras, envio de arquivos ou push notifications.

**O que o App TEM nesta versão:**
- Uma interface de altíssimo nível (Enterprise) densa e baseada em softwares SaaS (como Jira e Power BI).
- 3 Perfis de Usuário (Operador, Gestor, Líder) com fluxos isolados via *Navigation Compose*.
- Uma camada de dados simulada (`AppRepository` + `MockData`) com estado reativo através de `StateFlow`.
- Interatividade total: É possível criar, aprovar, visualizar KPIs fluindo em tempo real dentro do ciclo de vida da aplicação.

## 2. Tecnologias Utilizadas
- **Linguagem**: Kotlin 1.9.22
- **Framework UI**: Jetpack Compose (Material Design 3) com componentes personalizados e tipografia de alta densidade.
- **Gráficos**: Uso 100% nativo de APIs do `Canvas` e `Box` do Compose para gerar gráficos de barra sem dependências externas.
- **Arquitetura**: MVVM (Model-View-ViewModel) Simples.
- **Gerenciamento de Estado**: StateFlow e Coroutines.
- **SDK Alvo**: API 34 (Android 14) / **SDK Mínimo**: API 24 (Android 7.0).

## 3. Funcionalidades Implementadas por Perfil

### 3.1. Operador (Captura e Engajamento)
Focado no chão de fábrica e equipes de base.
- **Dashboard de Gamificação ("Enterprise Card")**: Exibe os Pontos de Experiência (XP). O operador ganha 10 XP por ideia enviada e 50 XP por aprovada. Possui os níveis: *Semente da Inovação (<100)*, *Agente de Mudança (<300)* e *Visão Águia (300+)*.
- **Visão de Pipeline (`PipelineOverview`)**: Uma trilha interativa que mostra quantas de suas ideias estão na "Fila", "Análise" ou "Sucesso".
- **Lista de Ideias (`CompactEnterpriseCard`)**: Cards densos com visualização de *Tags* geradas pela categoria, data, status codificado por cores e o "Impacto Esperado".
- **Cadastro de Ideia**: Um modal simples para cadastrar: Título, Problema/Solução e Categoria.
- **Aba de Diretrizes**: Acesso à leitura dos pilares estratégicos do Grupo Águia Branca.

### 3.2. Gestor (Curadoria e Tático)
Focado em aprovação ágil e controle de execução.
- **Funil Dinâmico com Filtros (`FilterChips`)**: Lista apenas as ideias pendentes ("Cadastradas" ou "Em Análise"). Possui *Chips* automáticos baseados nas Tags das ideias para filtragem rápida na tela.
- **Card de Curadoria (`CuratorshipIdeaCard`)**: Focado na aprovação rápida, exibindo a inicial do autor (estilo avatar) e um botão de ação direto no card.
- **Aprovação e Automação ("Seamless Flow")**: Ao aprovar uma ideia e selecionar sua Prioridade (Alta, Média, Baixa), o aplicativo dispara uma **automação simulada**. Ele abre imediatamente o "Modal de Novo Projeto" com o título e a descrição da ideia já pré-preenchidos.
- **Gestão de Projetos (`EnterpriseProjectCard`)**: Uma aba que mostra todos os projetos em andamento, exibindo o estágio atual, prazos, investimento financeiro (`CAPEX/OPEX`) e uma barra linear real de progresso percentual.

### 3.3. Líder (Visão Executiva C-Level)
Focado em controle, visibilidade de métricas e portfólio.
- **Resumo Executivo (KPIs)**:
  - **ROI Global**: Calculado matematicamente em memória a partir da soma dos investimentos e retornos financeiros dos projetos.
  - **Lucro Líquido**: Retorno menos Investimento. Cores dinâmicas (Verde se positivo, Vermelho se negativo).
  - Comparativos `YoY` (Year-Over-Year) falsos, apenas como métrica de demonstração de UX.
- **Gráfico de Barras Nativo (`ProjectsBarChart`)**: Um gráfico desenhado do zero mostrando a distribuição proporcional entre Projetos "Concluídos" e "Em Andamento".
- **Top Projetos por Impacto**: Uma lista gerada via algoritmo (método `.sortedByDescending` no ViewModel) que ordena os top 3 projetos de maior `retornoFinanceiro`.
- **Aba de Portfólio**: Uma listagem clássica de todos os projetos (ativos e concluídos) e seus status.

## 4. Estrutura de Dados e Lógica (Mock)
Os dados estão fixos em `MockData.kt` e focam na realidade corporativa (Logística e Frota):
- **User**: "João Operador", "Maria Gestora", "Carlos Líder".
- **Idea**: Campos de *impactoEsperado* (ex: "Redução de 15% no Diesel") e *economiaGeradaEstimada*. O Lifecycle base é: `CADASTRADA` -> `APROVADA` -> `CONVERTIDA_PROJETO`.
- **Project**: Campos de *investimento*, *retornoFinanceiro*, *progressoPercentual*, *prazoFinal*, e *comparativoYOY*.

## 5. Instruções para Execução e Troubleshooting
1. Extrair o arquivo `InovaGAB_Sprint1.zip`.
2. No Android Studio, selecione **Open** e aponte para a pasta descompactada `InovaGAB` (Não abra o repositório raiz inteiro).
3. O Android Studio fará o _sync_ do Gradle (O Gradle está forçado para a versão `8.4` no arquivo *wrapper* para evitar quebras com o Material Design 3).
4. O `gradle.properties` possui a flag `-Xmx2048m` para impedir travamentos de Out Of Memory ao compilar o Jetpack Compose.
5. Fazer login com os usuários de teste (Senha padrão: `123456`). Email sugeridos: `operador@gab.com`, `gestor@gab.com` ou `lider@gab.com`.
