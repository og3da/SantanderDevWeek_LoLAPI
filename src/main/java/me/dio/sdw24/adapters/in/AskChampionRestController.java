package me.dio.sdw24.adapters.in;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.dio.sdw24.adapters.in.exception.GlobalExceptionHandler;
import me.dio.sdw24.application.AskChampionUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Campeões (champions)", description = "Endpoints do domínio de campeões do LOL.")
@RestController
@RequestMapping("/champions")
public record AskChampionRestController(AskChampionUseCase useCase) {

    static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @CrossOrigin
    @PostMapping("/{championId}/ask")
    public AskChampionResponse askChampion(@PathVariable Long championId, @RequestBody AskChampionRequest request) {

        logger.info("=== QUESTION RECIEVED: %s ===".formatted(request.question()));
        String answer = useCase().askChampion(championId, request.question());

        return new AskChampionResponse(answer);
    }

    public record AskChampionRequest(String question) {
    }

    public record AskChampionResponse(String answer) {
    }
}