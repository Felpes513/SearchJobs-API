package com.searchjobs.api.infrastructure.ai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.ChatModel;
import com.searchjobs.api.domain.port.out.AiExtractionPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenAiExtractionAdapter implements AiExtractionPort {

    private final OpenAIClient client;
    private final String model;

    public OpenAiExtractionAdapter(
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.model}") String model
    ) {
        this.client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
        this.model = model;
    }

    @Override
    public String extractResumeData(String resumeText) {
        String prompt = buildPrompt(resumeText);

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.of(model))
                .addUserMessage(prompt)
                .build();

        ChatCompletion completion = client.chat().completions().create(params);

        return completion.choices().get(0).message().content().orElse("{}");
    }

    private String buildPrompt(String resumeText) {
        return """
            Você é um extrator de informações de currículos. Analise o texto abaixo e retorne APENAS um JSON válido, sem markdown, sem explicações, somente o JSON.
            
            O JSON deve ter exatamente esta estrutura:
            {
              "nome": "string",
              "email": "string",
              "telefone": "string",
              "skills": ["string"],
              "experiencias": [
                {
                  "cargo": "string",
                  "empresa": "string",
                  "descricao": "string",
                  "dataInicio": "string",
                  "dataFim": "string"
                }
              ],
              "certificacoes": [
                {
                  "nome": "string",
                  "instituicao": "string",
                  "dataObtencao": "string"
                }
              ],
              "projetos": [
                {
                  "nome": "string",
                  "descricao": "string",
                  "stack": "string",
                  "link": "string"
                }
              ]
            }
            
            REGRAS IMPORTANTES para o campo "skills":
            - Extraia APENAS tecnologias, linguagens, frameworks e ferramentas específicas
            - Exemplos corretos: "Java", "Spring Boot", "Angular", "PostgreSQL", "Docker", "Cypress", "Git"
            - Exemplos INCORRETOS: "Desenvolvimento Full Stack", "Inteligencia Artificial", "Qualidade de Software"
            - Cada skill deve ter no máximo 3 palavras
            - Não inclua áreas genéricas, apenas tecnologias concretas
            
            Se alguma informação não estiver presente, use null para strings e [] para arrays.
            
            Texto do currículo:
            %s
            """.formatted(resumeText);
    }
}