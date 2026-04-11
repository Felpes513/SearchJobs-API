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

    private final String model;

    public OpenAiExtractionAdapter(@Value("${openai.model}") String model) {
        this.model = model;
    }

    @Override
    public String extractResumeData(String prompt, String apiKey) {
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.of(model))
                .addUserMessage(prompt)
                .build();

        ChatCompletion completion = client.chat().completions().create(params);

        return completion.choices().get(0).message().content().orElse("{}");
    }
}
