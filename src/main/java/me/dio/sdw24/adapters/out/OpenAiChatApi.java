package me.dio.sdw24.adapters.out;

import feign.FeignException;
import feign.RequestInterceptor;
import me.dio.sdw24.adapters.in.exception.GlobalExceptionHandler;
import me.dio.sdw24.domain.ports.GenerativeAiApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@ConditionalOnProperty(name = "generative-ai.provider", havingValue = "OPENAI", matchIfMissing = true)
@FeignClient(name = "openAiChatApi", url = "${openai.base-url}", configuration = OpenAiChatApi.Config.class)
public interface OpenAiChatApi extends GenerativeAiApi {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @PostMapping("/v1/chat/completions")
    OpenAiChatCompletionResponse chatCompletion(OpenAiChatCompletionRequest request);

    /** EXEMPLO REQUEST OPEN AI
     * '{
     *     "model": "gpt-3.5-turbo",
     *     "messages": [
     *       {
     *         "role": "system",
     *         "content": "You are a helpful assistant."
     *       },
     *       {
     *         "role": "user",
     *         "content": "Who won the world series in 2020?"
     *       }
     *     ]
     *   }'
     */

    /** EXEMPLO RESPONSE OPEN AI
     * {
     *   "choices": [
     *     {
     *       "index": 0,
     *       "message": {
     *         "content": "The 2020 World Series was played in Texas at Globe Life Field in Arlington.",
     *         "role": "assistant"
     *       },
     *       "logprobs": null
     *     }
     *   ]
     * }
     */

    @Override
    default String generateContent(String objective, String context) {

        String model = "gpt-3.5-turbo";
        List<Message> messages = List.of(
                new Message("system", objective),
                new Message("user", context)
        );
        OpenAiChatCompletionRequest request = new OpenAiChatCompletionRequest(model, messages);

        try {
            OpenAiChatCompletionResponse response = chatCompletion(request);
            String responseText = response.choices().getFirst().message().content();
            if (!responseText.isEmpty())
                logger.info("=== QUESTION RESPONSE: %s (using %s AI) ===".formatted(responseText, "OpenAI"));
            return responseText;
        }
        catch (FeignException httpError) {
            String errorMessage = httpError.getMessage();
            logger.error(errorMessage);
            return "Erro de comunicação com a API OPENAI";
        } catch (Exception unexpectedError) {
            String errorMessage = unexpectedError.getMessage();
            logger.error(errorMessage);
            return "O retorno da API OPENAI não tem os dados esperados";
        }
    }

    record OpenAiChatCompletionRequest(String model, List<Message> messages) {
    }

    record OpenAiChatCompletionResponse(List<Choice> choices) {
    }

    record Choice(Message message) {
    }

    record Message(String role, String content) {
    }

    class Config {
        @Bean
        public RequestInterceptor apiKeyRequestInterceptor(@Value("${openai.api-key}") String apiKey) {
            return requestTemplate -> requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        }
    }
}
