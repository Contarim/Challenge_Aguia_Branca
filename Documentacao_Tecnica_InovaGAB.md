# InovaGAB - Documentação Técnica Oficial (Sprint 1 - Versão Enterprise V2)

## 1. Visão Geral (O que é e o que NÃO é)
O InovaGAB é um protótipo nativo para Android construído como um MVP corporativo focado na experiência de usuário (UX) e demonstração de fluxo tático de Gestão de Projetos e Inovação.

> [!WARNING]
> **O que o App NÃO TEM nesta versão:**
> - Não possui backend, API ou Firebase.
> - Não possui banco de dados persistente (SQLite/Room). Se fechar o app, as novas ideias não são salvas no disco.
> - Não possui autenticação real (senhas não são criptografadas, login é feito varrendo uma lista mockada em memória). **Senha padrão para todos os usuários: 123**.

**O que o App TEM nesta versão:**
- Uma interface de altíssimo nível (Enterprise) densa e baseada em softwares de mercado (como Jira e Power BI).
- 3 Perfis de Usuário (Operador, Gestor, Líder) com fluxos isolados via *Navigation Compose*.
- Um motor inteligente nativo de Prazos e SLAs de Execução, que recalcula atrasos matematicamente todos os dias.
- Camada de dados simulada (`AppRepository` + `MockData`) com estado reativo através de `StateFlow`.
- Componentes Visuais Reutilizáveis (Chips de Status de Prazo, Chips de Risco, e Cards de Alerta Executivo).

## 2. Tecnologias Utilizadas
- **Linguagem**: Kotlin 1.9.22
- **Framework UI**: Jetpack Compose (Material Design 3).
- **Gerenciamento de Estado**: StateFlow e Coroutines.
- **Gráficos**: Uso 100% nativo de APIs do Compose (Canvas) sem bibliotecas externas.
- **Manipulação de Data**: `java.util.Calendar` e `SimpleDateFormat` na classe *DateUtils* garantindo retrocompatibilidade (API Mínima 24).
- **UX & Máscaras de Input**: Implementação de `VisualTransformation` customizada para máscaras de **Data (dd/MM/yyyy)** e **Moeda (R$)** com validação de entrada em tempo real.
- **SDK Alvo**: API 34 (Android 14) / **SDK Mínimo**: API 24 (Android 7.0).

## 3. Arquitetura de Dados (As Novas Entidades de V2)
O aplicativo evoluiu consideravelmente sua arquitetura de dados (SaaS-like):
- **Ideias (`Idea.kt`)**: Cadastradas por operadores contendo: `titulo`, `descricao`, `categoria`, `autorNome` e `prioridade`. Lifecycle: CADASTRADA -> EM ANALISE -> APROVADA -> CONVERTIDA_PROJETO.
- **Projetos (`Project.kt`)**: Contém lógica densa (Apenas instanciado por Gestores):
  - **Identificação**: `titulo`, `descricao`, `responsavelNome`, `areaResponsavel`.
  - **SLA e Tempo**: `dataInicioPrevista`, `dataFimPrevista`, `dataInicioReal`, `dataFimReal`.
  - **Estratégia**: `risco` (Baixo, Médio, Alto, Crítico) e `prioridade`.
  - **Métricas Financeiras**: `investimento`, `retornoFinanceiro`.
  - **Getters Computados em Tempo Real**: `lucroEstimado` (Retorno - Investimento), `prazoEmDias`, `diasRestantes`, e `statusPrazo` (No Prazo, Próximo do Vencimento, Atrasado, Concluído).

## 4. Funcionalidades Implementadas por Perfil

### 4.1. Operador (Captura de Base)
- **Gamificação Integrada**: Cards dinâmicos exibem a pontuação em XP (Níveis: Semente da Inovação, Agente de Mudança, Visão Águia).
- **Pipeline de Inovação**: Um tracking visual onde o operador vê exatamente em qual estágio da fila suas ideias submetidas estão paradas.
- O Operador não edita datas nem gere projetos, ele é o motor inicial do funil criativo.

### 4.2. Gestor (Curadoria Tática)
- **Funil de Ideias**: O gestor visualiza as ideias com `FilterChips` dinâmicos por categoria.
- **Conversão Ideia para Projeto (Seamless Flow)**: O botão "Avaliar Ideia" abre o Modal de Aprovação Tática. Seguindo a separação de responsabilidades, o gestor foca no **Planejamento Operacional**: define a *Prioridade (Baixa a Crítica)*, o *Risco*, a *Área Responsável* e as *Datas Estimadas*.
- **Omissão Financeira**: Seguindo as novas regras de negócio, o gestor não visualiza nem preenche campos de CAPEX ou Retorno, que são exclusivos da liderança.
- **Gestão de Projetos**: Os projetos em andamento aparecem numa lista densa de acompanhamento com barras de progresso, `StatusPrazoChip` e chips de Risco.

### 4.3. Líder (Dashboard C-Level)
O Dashboard executivo foi aprimorado para foco em SLA e retorno de investimento:
- **Alertas Vivos**: Componente nativo detecta se no repositório há Projetos com status "Atrasado". Caso positivo, gera um Header vermelho na tela solicitando atenção executiva.
- **KPIs Financeiros**: O app calcula ativamente as somas do portfólio apontando o Lucro Estimado e o % de ROI Global da Inovação da empresa em tempo real.
- **Indicadores de Prazo**: Card apontando exatamente a divisão entre projetos No Prazo vs Vencendo (Risco).
- **Top Projetos por ROI**: Lista inteligente via algoritmo `.sortedByDescending` que destaca apenas o top 3 do portfólio.
- **Gestão e Edição de Portfólio (Full Control)**: O Líder agora possui um **Modal de Edição Completo**. Ao clicar em qualquer projeto, ele pode ajustar:
  - **Dados Financeiros**: Inserir e alterar CAPEX (Investimento) e Retorno Estimado.
  - **Cronograma**: Reajustar datas de início e fim.
  - **Status do Projeto**: Alterar entre *Planejado, Em andamento, Pausado, Concluído ou Cancelado*.
  - **Responsáveis**: Alterar o nome do responsável e o grupo/área.

## 5. Dinâmica de Dados Mockados Inteligentes
Para garantir que o app não perca a coerência dos prazos e status durante apresentações demoradas, os dados de teste na `MockData.kt` não foram criados com datas estáticas ("25/08/2026").
Utilizamos **offsets de tempo calculados**.
Exemplo de um Projeto Atrasado: `Data Fim Prevista = Hoje - 5 dias`.
Isso garante que sempre que você abrir o app para demonstrar a plataforma, as tags de *Próximo do Vencimento* e *Atrasado* funcionem perfeitamente.

## 6. Solução de Problemas de Compilação
- Certifique-se de realizar o *Sync* do Gradle na pasta raiz `InovaGAB`.
- Evite atualizar para Gradle Plugin 8.5+ sem antes validar dependências Compose, o projeto foi arquitetado e estabilizado na versão **8.4** do wrapper (`gradle/wrapper/gradle-wrapper.properties`).
- **Conflitos de Material 3**: O projeto utiliza a BOM do Compose e a versão estável `1.1.2` do Material 3 para evitar erros de classes duplicadas (`Duplicate class androidx.compose.material3.tokens.TypographyTokensKt`). Não force versões superiores a `1.2.0` sem migrar todo o SDK.
- Se houver lentidão na compilação, o `gradle.properties` original com `-Xmx2048m` já está injetado no repositório para evitar *GC Overhead limit exceeded* da JVM.
