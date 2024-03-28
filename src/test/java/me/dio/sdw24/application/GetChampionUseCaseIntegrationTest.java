package me.dio.sdw24.application;

import me.dio.sdw24.domain.model.Champions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class GetChampionUseCaseIntegrationTest {

    @Autowired
    private GetChampionUseCase getChampionUseCase;

    @Test
    public void testGetChampions() {
        Optional<Champions> champion = getChampionUseCase.findById(1L);

        Assertions.assertNotNull(champion);
    }
}
