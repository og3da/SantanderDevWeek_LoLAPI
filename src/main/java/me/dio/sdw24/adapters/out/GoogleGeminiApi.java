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
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@ConditionalOnProperty(name = "generative-ai.provider", havingValue = "GEMINI")
@FeignClient(name = "googleGeminiApi", url = "${gemini.base-url}", configuration = GoogleGeminiApi.Config.class)
public interface GoogleGeminiApi extends GenerativeAiApi {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @PostMapping("/v1beta/models/gemini-pro:generateContent")
    GeminiResponse chatCompletion(GeminiRequest request);

    /** EXEMPLO REQUEST  GEMINI
     '{
     "contents": [{
     "parts":[{
     "text": "Write a story about a magic backpack."}
     ]
     }]
     }'
     */

    /**
     * EXEMPLO RESPONSE  GEMINI
     * {
     * "candidates": [
     * {
     * "content": {
     * "parts": [
     * {
     * "text": "In the quaint town of Willow Creek, nestled amidst rolling hills and whispering willows, there lived an ordinary boy named Ethan. Ethan's life took an extraordinary turn the day he stumbled upon an enigmatic backpack hidden in the depths of his attic.\n\nCuriosity ignited within Ethan as he lifted the worn leather straps and unzipped its mysterious contents. Inside lay a shimmering array of vibrant objects and peculiar trinkets. There was a glowing orb that pulsated with an ethereal glow, a feather that seemed to have a life of its own, and a small, enigmatic key.\n\nAs Ethan explored each item, he realized they possessed astonishing abilities. The orb illuminated his path, casting a warm glow in the darkest of nights. The feather granted him the power of flight, allowing him to soar through the skies with newfound freedom. And the key opened a portal to a hidden world, a realm of endless wonder.\n\nArmed with his magical backpack, Ethan embarked on countless adventures. He flew over the towering mountains of Willow Creek, exploring their hidden secrets. He navigated the treacherous depths of the Enchanted Forest, where he encountered mythical creatures and ancient spirits. And he ventured into distant, unknown lands, uncovering lost civilizations and forgotten treasures.\n\nWith each adventure, Ethan's knowledge and abilities grew. He learned to harness the power of his backpack wisely, using its magic to help others and protect the world from evil forces. The backpack became an extension of himself, a symbol of hope and wonder in the face of adversity.\n\nAs the years went by, Ethan's reputation as the boy with the magic backpack spread far and wide. People from all walks of life came to him, seeking his guidance and protection. And Ethan never hesitated to lend a helping hand, using his extraordinary abilities to make the world a better place.\n\nIn the end, the magic backpack became more than just a collection of objects. It was a representation of Ethan's unwavering spirit, his boundless imagination, and his unwavering belief in the power of dreams. And as long as Ethan carried it with him, the magic of Willow Creek would live on, illuminating the darkest corners of the world with hope, wonder, and the limitless possibilities that resided within the heart of a child."
     * }
     * ],
     * "role": "model"
     * },
     */

    @Override
    default String generateContent(String objective, String context) {

        List<Content> contents = List.of(
                new Content(List.of(
                        new Part(objective + context)
                ))
        );
        GeminiRequest request = new GeminiRequest(contents);

        try {
            GeminiResponse response = chatCompletion(request);
            String responseText = response.candidates().getFirst().content().parts().getFirst().text();
            if (!responseText.isEmpty())
                logger.info("=== QUESTION RESPONSE: %s (using %s AI) ===".formatted(responseText, "Google Gemini"));
            return responseText;
        } catch (FeignException httpError) {
            String errorMessage = httpError.getMessage();
            logger.error(errorMessage);
            return "Erro de comunicação com a API GEMINI";
        } catch (Exception unexpectedError) {
            String errorMessage = unexpectedError.getMessage();
            logger.error(errorMessage);
            return "O retorno da API GEMINI não tem os dados esperados";
        }
    }

    record GeminiRequest(List<Content> contents) {
    }

    record GeminiResponse(List<Candidate> candidates) {
    }

    record Content(List<Part> parts) {
    }

    record Part(String text) {
    }

    record Candidate(Content content) { }

    class Config {
        @Bean
        public RequestInterceptor apiKeyRequestInterceptor(@Value("${gemini.api-key}") String apiKey) {
            return requestTemplate -> requestTemplate.query("key", apiKey);
        }
    }
}
