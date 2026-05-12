# InovaGAB - Challenge Grupo Águia Branca 🦅🚀

Bem-vindo ao repositório oficial do **InovaGAB**, a plataforma corporativa de gestão de inovação e projetos desenvolvida para o **Desafio Semestral da FIAP com o Grupo Águia Branca**.

O InovaGAB é um aplicativo mobile nativo Android desenhado para transformar a forma como as ideias nascem na operação e se tornam projetos de alto impacto (ROI) gerenciados pela diretoria.

---

## 🎯 Objetivo do Projeto
O InovaGAB foi criado para conectar as pontas da pirâmide corporativa:
1. **Fomentar o Intraempreendedorismo**: Permitir que operadores de base (Logística, Manutenção, etc.) tragam soluções para os problemas reais do dia a dia.
2. **Curadoria Inteligente**: Fornecer aos gestores as ferramentas para analisar, priorizar e aprovar as ideias com base em prazos, riscos e investimentos.
3. **Visão Executiva (C-Level)**: Dar à liderança um painel (Dashboard) com indicadores de saúde, análise de SLA (Prazos) e cálculo de Retorno sobre Investimento (ROI).

---

## 📱 Perfil de Usuários (A Jornada InovaGAB)

A aplicação é dividida em 3 experiências perfeitamente isoladas:

- 👷 **Operador (A Semente)**: Consulta diretrizes estratégicas da empresa, registra suas ideias, acompanha o status do seu funil e possui um sistema de gamificação embutido que recompensa a participação.
- 🧑‍💼 **Gestor (O Filtro)**: Triagem avançada das ideias propostas. É o responsável por aprovar a ideia e transformá-la em projeto ativo (definindo prazos, riscos, área responsável e CAPEX estimado).
- 👔 **Liderança (O Resultado)**: Tem uma visão panorâmica do portfólio de projetos, com alertas automatizados para projetos atrasados e totalização do retorno financeiro.

---

## 🛠️ Tecnologias e Arquitetura

O InovaGAB foi construído do zero focando em uma UI/UX Premium (Enterprise Level) e com arquitetura reativa sólida:

- **Linguagem**: Kotlin (1.9.22)
- **UI Toolkit**: Jetpack Compose (Material 3) com componentes 100% nativos (Canvas para gráficos, sem dependências de UI externas).
- **Gerenciamento de Estado**: StateFlow e Coroutines para uma arquitetura Unidirectional Data Flow.
- **Engine Tático**: Sistema próprio em memória (`DateUtils`) para cálculo e simulação de Prazos e SLAs dinâmicos (Retrocompatível até Android 7.0 - API 24).

---

## 📄 Documentação Técnica e Demonstração

Para entender como a aplicação foi construída a nível de código, como os dados mockados reagem em tempo real e como compilar o aplicativo no seu Android Studio sem quebrar dependências, **leia a nossa documentação técnica:**

👉 **[Acessar a Documentação Técnica Oficial (Documentacao_Tecnica_InovaGAB.md)](./Documentacao_Tecnica_InovaGAB.md)**

---

**FIAP - Challenge Semestral**  
*Desenvolvido para revolucionar a logística e a eficiência do Grupo Águia Branca.*
