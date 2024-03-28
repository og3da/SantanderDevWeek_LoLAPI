package me.dio.sdw24.application;

import me.dio.sdw24.domain.model.Champions;
import me.dio.sdw24.domain.ports.ChampionsRepository;

import java.util.Optional;

public record GetChampionUseCase (ChampionsRepository repository) {

    public Optional<Champions> findById(Long id) {
        return repository.findById(id);
    }
}
