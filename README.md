# SearchJobs API

> Encontre vagas que fazem sentido para o seu perfil — com a ajuda de inteligência artificial.

---

## O que é o SearchJobs?

O **SearchJobs** nasceu de uma dor real: procurar emprego é cansativo. São dezenas de plataformas, vagas genéricas que não têm nada a ver com você e horas perdidas lendo descrições que não combinam com o seu perfil.

A ideia é simples — **e se um sistema pudesse ler o seu currículo, entender quem você é como profissional e te mostrar só as vagas que realmente valem a pena?**

É exatamente isso que o SearchJobs faz.

---

## Como funciona?

1. **Você envia seu currículo em PDF.** A IA extrai automaticamente suas skills, experiências, certificações e projetos — sem você precisar preencher nada manualmente.

2. **O sistema busca vagas reais no mercado**, usando o seu cargo desejado como base, conectado a APIs de emprego com milhares de oportunidades atualizadas.

3. **A IA analisa cada vaga e gera um score de compatibilidade** com o seu perfil — explicando por que você é (ou não é) um bom fit, quais são seus pontos fortes e quais gaps você teria que superar.

4. **Você vê só o que importa**, ordenado por relevância, com link direto para se candidatar.

---

## Funcionalidades

- 📄 **Upload e extração inteligente de currículo** via IA (OpenAI GPT)
- 🔍 **Busca de vagas personalizada** com rankeamento por relevância
- 🤖 **Match com IA** — score de 0 a 100 para cada vaga com justificativa
- 👤 **Perfil profissional completo** — skills, experiências, certificações e projetos
- 🐙 **Sincronização com GitHub** — importa seus repositórios públicos como projetos automaticamente
- 🔐 **Autenticação segura** com JWT

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3 |
| Banco de dados | PostgreSQL |
| Inteligência Artificial | OpenAI GPT (gpt-4o-mini) |
| Busca de vagas | JSearch API (RapidAPI) |
| Leitura de PDF | Apache PDFBox |
| Autenticação | JWT (jjwt) |
| Cache | Caffeine |
| Documentação | SpringDoc OpenAPI (Swagger) |
| Infraestrutura | Docker + Docker Compose |

A arquitetura segue os princípios de **Arquitetura Hexagonal (Ports & Adapters)**, garantindo um código desacoplado, testável e fácil de evoluir.

---

## Por que foi desenvolvido?

O SearchJobs é um projeto pessoal criado com dois objetivos:

1. **Resolver um problema real** — tornar a busca de emprego mais inteligente e menos frustrante para desenvolvedores e profissionais de tecnologia.

2. **Consolidar conhecimentos** em desenvolvimento de APIs modernas com Java, integração com IA, boas práticas de arquitetura e uso de serviços externos.

---

## Próximos passos

O projeto está em evolução contínua. Algumas melhorias planejadas:

- [ ] Frontend web para consumo da API
- [ ] Hospedagem em VPS com domínio próprio
- [ ] Notificações por e-mail quando novas vagas compatíveis aparecerem
- [ ] Suporte a múltiplos idiomas nas buscas (inglês, espanhol)
- [ ] Histórico de candidaturas
- [ ] Integração com mais plataformas de vagas

---

## Rodando o projeto

### Com Docker (recomendado)

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/searchjobs-api.git
cd searchjobs-api

# Configure as variáveis de ambiente
cp .env.example .env
# Edite o .env com suas chaves de API

# Suba tudo com Docker Compose
docker compose up --build
```

A API estará disponível em `http://localhost:8080`.
A documentação interativa (Swagger) em `http://localhost:8080/swagger-ui.html`.

---

## Variáveis de ambiente

Veja o arquivo [`.env.example`](.env.example) para todas as variáveis necessárias.

Você vai precisar de:
- Uma conta na [OpenAI](https://platform.openai.com) para a chave de API da IA
- Uma chave da [JSearch no RapidAPI](https://rapidapi.com/letscrape-6bRBa3QguO5/api/jsearch) para busca de vagas
- Um banco PostgreSQL (já incluído no Docker Compose)

---

<p align="center">Desenvolvido por <strong>Felipe Souza Moreira</strong></p>