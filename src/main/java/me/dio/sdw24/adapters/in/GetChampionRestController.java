package me.dio.sdw24.adapters.in;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.dio.sdw24.application.GetChampionUseCase;
import me.dio.sdw24.domain.model.Champions;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Campeões (champions)", description = "Endpoints do domínio de campeões do LOL.")
@RestController
@RequestMapping("/champions")
public record GetChampionRestController(GetChampionUseCase useCase) {

    @GetMapping("/{championId}")
    public Optional<Champions> findById(@PathVariable Long championId) {

        return useCase.findById(championId);
    }
}
