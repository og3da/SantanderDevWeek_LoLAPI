package me.dio.sdw24.adapters.in;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.dio.sdw24.application.ListChampionsUseCase;
import me.dio.sdw24.domain.model.Champions;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Campeões (champions)", description = "Endpoints do domínio de campeões do LOL.")
@RestController
@RequestMapping("/champions")
public record ListChampionsRestController(ListChampionsUseCase useCase) {

    @CrossOrigin
    @GetMapping("/findAll")
    public List<Champions> findAll() {
        return useCase.findAll();
    }
}
